package com.maven.appointment;

import com.jfinal.aop.Aop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import com.maven.common.model.Appointment;
import com.maven.common.model.Doctor;
import com.maven.common.model.Patient;
import com.maven.common.model.RelationPatient;
import com.maven.common.model.User;
import com.maven.common.util.RetUtils;
import com.maven.doctorManage.DepartmentEnum;
import com.maven.doctorManage.UserService;
import com.maven.user.SexEnum;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 预约相关service
 */
public class AppointmentService {
    public static final AppointmentService me = Aop.get(AppointmentService.class);
    private Appointment dao = Appointment.dao;
    private UserService userSrc = UserService.me;

    /*当前医生已经预约的病人列表，支持翻页*/
    public Ret listAll(Integer pageSize, Integer pageNumber, String name, User userLogin) {

        //今天
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        SqlPara sqlPara = new SqlPara();
        StringBuffer sb = new StringBuffer("");
        //当前日期和医生id查询出已预约的病人列表
        sb.append("select * from t_appointment where doctor_id = ? ");
        //sb.append("select * from t_appointment where doctor_id = ? ");
        sqlPara.addPara(userLogin.getRelation());

        //病人姓名模糊查询条件
        if (StringUtils.isNotBlank(name)) {
            sb.append(" and real_patient_name like '%" + name + "%' ");
        }
        sb.append(" order by status,date");
        sqlPara.setSql(sb.toString());
        Page<Appointment> page = dao.paginate(pageNumber, pageSize, sqlPara);
        List<Appointment> list = page.getList();
        //取出所有给自己看病的病人id列表
        List<Integer> patientIds = list.stream().filter(e -> e.getIsSelf()).map(Appointment::getRealPatientId).collect(Collectors.toList());
        //取出所有给家属看病的家属id列表
        List<Integer> relationPatientIds = list.stream().filter(e -> !e.getIsSelf()).map(Appointment::getRealPatientId).collect(Collectors.toList());
        //所有给自己看病的病人列表
        Map<Integer, Patient> patientMap = userSrc.findPatientByIds(patientIds).stream().collect(Collectors.toMap(Patient::getId, e -> e, (o, n) -> n));
        //所有给家属看病的家属列表
        Map<Integer, RelationPatient> relationPatientMap = userSrc.findRelationPatientByIds(relationPatientIds).stream().collect(Collectors.toMap(RelationPatient::getId, e -> e, (o, n) -> n));
        //遍历list,放入性别，身份证，年龄
        list.forEach(e -> {
            if (e.getIsSelf()) {
                Patient patient = patientMap.containsKey(e.getRealPatientId()) ? patientMap.get(e.getRealPatientId()) : null;
                SexEnum sexEnum = SexEnum.getByValue(patient == null ? null : patient.getSex());
                e.put("sex", sexEnum == null ? "" : sexEnum.getText());
                e.put("id_num", patient == null ? "" : patient.getIdNum());
                e.put("age", patient == null ? "" : userSrc.getAge(patient.getBirthday()));
            } else {
                RelationPatient relationPatient = relationPatientMap.containsKey(e.getRealPatientId()) ? relationPatientMap.get(e.getRealPatientId()) : null;
                SexEnum sexEnum = SexEnum.getByValue(relationPatient == null ? null : relationPatient.getSex());
                e.put("sex", sexEnum == null ? "" : sexEnum.getText());
                e.put("id_num", relationPatient == null ? "" : relationPatient.getIdNum());
                e.put("age", relationPatient == null ? "" : userSrc.getAge(relationPatient.getBirthday()));
            }
            DepartmentEnum departmentEnum = DepartmentEnum.getByValue(e.getDepartment());
            StatusEnum statusEnum = StatusEnum.getByValue(e.getStatus());
            TimeEnum timeEnum = TimeEnum.getByValue(e.getTimeEnum());
            e.put("department_text", departmentEnum == null ? "" : departmentEnum.getText());
            e.put("status_text", statusEnum == null ? "" : statusEnum.getText());
            e.put("time_text", timeEnum == null ? "" : timeEnum.getText());
        });
        return Ret.ok().set("rows", page.getList()).set("total", page.getTotalRow());
    }
    /*当前医生今天已经预约的病人列表，支持翻页*/
    public Ret list(Integer pageSize, Integer pageNumber, String name, User userLogin) {

        //今天
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        SqlPara sqlPara = new SqlPara();
        StringBuffer sb = new StringBuffer("");
        //当前日期和医生id查询出已预约的病人列表
        sb.append("select * from t_appointment where doctor_id = ? and date >= str_to_date(?,'%Y-%m-%d %H:%i:%s') and date <= str_to_date(?,'%Y-%m-%d %H:%i:%s') ");
        //sb.append("select * from t_appointment where doctor_id = ? ");
        sqlPara.addPara(userLogin.getRelation());
        sqlPara.addPara(now + " 00:00:00");
        sqlPara.addPara(now + " 23:59:59");
        //病人姓名模糊查询条件
        if (StringUtils.isNotBlank(name)) {
            sb.append(" and real_patient_name like '%" + name + "%' ");
        }
        sb.append(" order by status,date");
        sqlPara.setSql(sb.toString());
        Page<Appointment> page = dao.paginate(pageNumber, pageSize, sqlPara);
        List<Appointment> list = page.getList();
        List<Integer> patientIds = list.stream().filter(e -> e.getIsSelf()).map(Appointment::getRealPatientId).collect(Collectors.toList());
        List<Integer> relationPatientIds = list.stream().filter(e -> !e.getIsSelf()).map(Appointment::getRealPatientId).collect(Collectors.toList());
        Map<Integer, Patient> patientMap = userSrc.findPatientByIds(patientIds).stream().collect(Collectors.toMap(Patient::getId, e -> e, (o, n) -> n));
        Map<Integer, RelationPatient> relationPatientMap = userSrc.findRelationPatientByIds(relationPatientIds).stream().collect(Collectors.toMap(RelationPatient::getId, e -> e, (o, n) -> n));
        list.forEach(e -> {
            //给自己看病的
            if (e.getIsSelf()) {
                Patient patient = patientMap.containsKey(e.getRealPatientId()) ? patientMap.get(e.getRealPatientId()) : null;
                SexEnum sexEnum = SexEnum.getByValue(patient == null ? null : patient.getSex());
                e.put("sex", sexEnum == null ? "" : sexEnum.getText());
                e.put("id_num", patient == null ? "" : patient.getIdNum());
                e.put("age", patient == null ? "" : userSrc.getAge(patient.getBirthday()));
            } else {//给家属看病
                RelationPatient relationPatient = relationPatientMap.containsKey(e.getRealPatientId()) ? relationPatientMap.get(e.getRealPatientId()) : null;
                SexEnum sexEnum = SexEnum.getByValue(relationPatient == null ? null : relationPatient.getSex());
                e.put("sex", sexEnum == null ? "" : sexEnum.getText());
                e.put("id_num", relationPatient == null ? "" : relationPatient.getIdNum());
                e.put("age", relationPatient == null ? "" : userSrc.getAge(relationPatient.getBirthday()));
            }
            DepartmentEnum departmentEnum = DepartmentEnum.getByValue(e.getDepartment());
            StatusEnum statusEnum = StatusEnum.getByValue(e.getStatus());
            TimeEnum timeEnum = TimeEnum.getByValue(e.getTimeEnum());
            e.put("department_text", departmentEnum == null ? "" : departmentEnum.getText());
            e.put("status_text", statusEnum == null ? "" : statusEnum.getText());
            e.put("time_text", timeEnum == null ? "" : timeEnum.getText());
        });
        return Ret.ok().set("rows", page.getList()).set("total", page.getTotalRow());
    }

    public Ret appointment(Integer department, Integer doctorId, boolean isSelf, Integer patientId, Date date, Integer time, User userLogin) {
        DepartmentEnum departmentEnum = DepartmentEnum.getByValue(department);
        TimeEnum timeEnum = TimeEnum.getByValue(time);
        if (departmentEnum == null || doctorId == null || (!isSelf && patientId == null) || date == null || timeEnum == null) {
            return RetUtils.parameterFail();
        }
        Doctor doctor = Doctor.dao.findById(doctorId);
        if (doctor == null || !doctor.getDepartment().equals(department)) {
            return RetUtils.fail("无此医生");
        }
        LocalDate now = LocalDate.now();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (localDate.compareTo(now) <= 0) {
            return RetUtils.fail("请至少提前一天预约");
        }
        TimeListBean timeListBean = getTimeListBeans(doctorId, localDate).stream().filter(e -> e.getTimeValue().equals(timeEnum.getValue())).findFirst().get();
        if (timeListBean.getRemainingNum().compareTo(0) <= 0) {
            return RetUtils.fail("该时间段已预约满，请选择其他时段");
        }
        Appointment appointment = new Appointment();
        appointment.setPatientId(userLogin.getRelation());
        appointment.setIsSelf(isSelf);
        appointment.setDate(date);
        appointment.setDepartment(department);
        appointment.setDoctorId(doctorId);
        appointment.setDoctorName(doctor.getName());
        appointment.setTimeEnum(timeEnum.getValue());
        if (!isSelf) {
            RelationPatient patient = RelationPatient.dao.findById(patientId);
            if (patient == null || !patient.getPatientId().equals(userLogin.getRelation())) {
                return RetUtils.fail("无此其他就诊人");
            }
            appointment.setRealPatientId(patientId);
            appointment.setRealPatientName(patient.getName());
        } else {
            appointment.setRealPatientId(userLogin.getRelation());
            appointment.setRealPatientName(userLogin.getName());
        }
        return appointment.save() ? RetUtils.ok("预约成功") : RetUtils.fail("预约失败");
    }

    /*根据医生id和日期获取可以预约的列表*/
    public Ret getTimeList(Integer doctorId, Date date) {
        if (doctorId == null || date == null) {
            return RetUtils.parameterFail();
        }
        LocalDate now = LocalDate.now();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (localDate.compareTo(now) <= 0) {
            return RetUtils.fail("请至少提前一天预约");
        }
        return Ret.ok().set("values", getTimeListBeans(doctorId, localDate));
    }

    /*根据医生id和日期按照时间段分组获取预约信息*/
    private List<TimeListBean> getTimeListBeans(Integer doctorId, LocalDate localDate) {
        //获取某医生某天已预约列表
        List<Appointment> appointments = dao.find("select * from t_appointment where Date(date) = ? and doctor_id = ?", localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), doctorId);
        //根据时间段分组，计算出每个时间段的预约数，放到map
        Map<Integer, Long> map = appointments.stream().collect(Collectors.groupingBy(Appointment::getTimeEnum, Collectors.counting()));
        //分组后的详细列表放到一天的工作安排中，即使没有预约，也you一条数据
        List<TimeListBean> list = new ArrayList<>();
        //遍历工作时间
        for (TimeEnum timeEnum : TimeEnum.values()) {
            Integer total = PropKit.getInt("timeLimit");//限制预约人数
            Integer reservedNum = 0;//该时间段已预约人数
            if (map.containsKey(timeEnum.getValue())) {
                reservedNum = map.get(timeEnum.getValue()).intValue();
            }
            list.add(new TimeListBean(timeEnum.getValue(), timeEnum.getText(), total, reservedNum, total - reservedNum));
        }
        return list;
    }

    /*我的预约记录*/
    public Page<Appointment> myAppointmentList(Integer pageSize, Integer pageNumber, Integer patientId) {
        Page<Appointment> page = dao.paginate(pageNumber, pageSize, "select * ", " from t_appointment where patient_id = ?", patientId);
        for (Appointment appointment : page.getList()) {
            DepartmentEnum departmentEnum = DepartmentEnum.getByValue(appointment.getDepartment());
            StatusEnum statusEnum = StatusEnum.getByValue(appointment.getStatus());
            TimeEnum timeEnum = TimeEnum.getByValue(appointment.getTimeEnum());
            appointment.put("department_text", departmentEnum == null ? "" : departmentEnum.getText());
            appointment.put("status_text", statusEnum == null ? "" : statusEnum.getText());
            appointment.put("time_text", timeEnum == null ? "" : timeEnum.getText());
        }
        return page;
    }
}