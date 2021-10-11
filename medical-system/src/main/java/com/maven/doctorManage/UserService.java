package com.maven.doctorManage;

import com.jfinal.aop.Aop;
import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.maven.common.model.Doctor;
import com.maven.common.model.Patient;
import com.maven.common.model.RelationPatient;
import com.maven.common.model.User;
import com.maven.common.util.RetUtils;
import com.maven.user.SexEnum;
import com.maven.user.UserTypeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class UserService {
    public static final UserService me = Aop.get(UserService.class);
    private User dao = User.dao;

    public Ret list(Integer pageNumber, Integer pageSize) {
        Page<User> page = dao.paginate(pageNumber, pageSize, "select u.id,u.account,u.name,u.sex,u.relation,d.department,d.introduction"
                , "from t_user u left join t_doctor d on u.relation = d.id where u.user_type = ?", UserTypeEnum.DOCTOR.getValue());
        page.getList().forEach(e -> {
            DepartmentEnum departmentEnum = DepartmentEnum.getByValue(e.getInt("department"));
            SexEnum sexEnum = SexEnum.getByValue(e.getSex());
            e.put("department_text", departmentEnum == null ? "" : departmentEnum.getText());
            e.put("sex_text", sexEnum == null ? "" : sexEnum.getText());
        });
        return Ret.ok().set("rows", page.getList()).set("total", page.getTotalRow());
    }

    public Ret checkAccount(String account) {
        SqlPara sqlPara = new SqlPara();
        sqlPara.setSql("select * from t_user where account = ?")
                .addPara(account);
        User user = dao.findFirst(sqlPara);
        if (user != null) {
            return RetUtils.fail("该账号已存在");
        }
        return Ret.ok();
    }

    @Before(Tx.class)
    public Ret saveDoctor(User user, Integer department, String introduction) {
        DepartmentEnum departmentEnum = DepartmentEnum.getByValue(department);
        SexEnum sexEnum = SexEnum.getByValue(user.getSex());
        if (StringUtils.isBlank(user.getAccount()) || StringUtils.isBlank(user.getPassword()) || departmentEnum == null
                || StringUtils.isBlank(user.getName()) || sexEnum == null) {
            return RetUtils.parameterFail();
        }
        Ret ret = checkAccount(user.getAccount());
        if (ret.isFail()) {
            return ret;
        }
        Doctor doctor = new Doctor();
        doctor.setDepartment(department);
        doctor.setName(user.getName());
        doctor.setSex(user.getSex());
        doctor.setIntroduction(introduction);
        doctor.save();
        user.setRelation(doctor.getId());
        user.setUserType(UserTypeEnum.DOCTOR.getValue());
        return RetUtils.save(user.save());
    }

    @Before(Tx.class)
    public Ret updateDoctor(User model, Integer department, String introduction) {
        DepartmentEnum departmentEnum = DepartmentEnum.getByValue(department);
        SexEnum sexEnum = SexEnum.getByValue(model.getSex());
        if (model.getId() == null || departmentEnum == null || StringUtils.isBlank(model.getName()) || sexEnum == null) {
            return RetUtils.parameterFail();
        }
        User user = dao.findById(model.getId());
        if (user == null || !user.getUserType().equals(UserTypeEnum.DOCTOR.getValue())) {
            return RetUtils.fail("无此用户");
        }
        Doctor doctor = Doctor.dao.findById(user.getRelation());
        if (doctor == null) {
            return RetUtils.fail("无此用户");
        }
        doctor.setDepartment(department);
        doctor.setName(user.getName());
        doctor.setSex(user.getSex());
        doctor.setIntroduction(introduction);
        doctor.update();
        user.setName(model.getName());
        user.setSex(model.getSex());
        return RetUtils.update(user.update());
    }

    @Before(Tx.class)
    public Ret deleteDoctor(Integer id) {
        if (id == null) {
            return RetUtils.parameterFail();
        }
        User user = dao.findById(id);
        if (user == null || !user.getUserType().equals(UserTypeEnum.DOCTOR.getValue())) {
            return RetUtils.fail("无此用户");
        }
        Doctor doctor = Doctor.dao.findById(user.getRelation());
        if (doctor == null) {
            return RetUtils.fail("无此用户");
        }
        doctor.delete();
        return RetUtils.delete(user.delete());
    }

    /*根据生日计算年龄*/
    public int getAge(Date birthday) {
        DateFormat df = new SimpleDateFormat("yyyy");
        int year = Integer.parseInt(df.format(birthday));
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int curYear = c.get(Calendar.YEAR);
        return curYear - year;
    }

    public Ret modifyPassword(User user, String oldPassword, String password, String confirmPassword) {
        if (StringUtils.isBlank(password) || StringUtils.isBlank(confirmPassword)) {
            return RetUtils.fail("密码不能为空");
        }
        if (!password.equals(confirmPassword)) {
            return RetUtils.fail("两次密码输入不一致");
        }
        if (user == null) {
            return RetUtils.fail("无此用户");
        }
        if (!user.getPassword().equals(oldPassword)) {
            return RetUtils.fail("旧密码错误");
        }
        if (user.getPassword().equals(password)) {
            return RetUtils.fail("新密码不能与旧密码一致");
        }
        user.setPassword(password);
        return (user.update() ? RetUtils.ok("修改成功，请重新登录") : RetUtils.fail("密码修改失败"));
    }

    /*根据病人id列表查询出详情列表*/
    public List<Patient> findPatientByIds(Collection<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return Patient.dao.find("select * from t_patient where id in (?) ", StringUtils.join(ids, ","));
    }

    /*根据病人家属id列表查询看病的那个病人家属详情列表*/
    public List<RelationPatient> findRelationPatientByIds(Collection<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return RelationPatient.dao.find("select * from t_relation_patient where id in (?) ", StringUtils.join(ids, ","));
    }

    public List<Doctor> findDoctorByDepartment(Integer department) {
        return Doctor.dao.find("select id,name from t_doctor where department = ?", department);
    }

    public Ret findMyRelationPatients(Integer pageNumber, Integer pageSize, Integer patientId) {
        Page<RelationPatient> paginate = RelationPatient.dao.paginate(pageNumber, pageSize, "select *", " from t_relation_patient where patient_id = ?", patientId);
        return Ret.ok().set("rows", paginate.getList()).set("total", paginate.getTotalRow());
    }

    /*新增其他就诊人*/
    public Ret saveRelationPatient(RelationPatient patient, User userLogin) {
        SexEnum sexEnum = SexEnum.getByValue(patient.getSex());
        if (StringUtils.isBlank(patient.getName()) || sexEnum == null || patient.getBirthday() == null || StringUtils.isBlank(patient.getIdNum()) || StringUtils.isBlank(patient.getPhone())) {
            return RetUtils.parameterFail();
        }
        patient.setPatientId(userLogin.getRelation());
        return RetUtils.save(patient.save());
    }

    public List<RelationPatient> findRelationPatientSel(User userLogin) {
        return RelationPatient.dao.find("select id,name from t_relation_patient where patient_id = ?", userLogin.getRelation());
    }

    public Ret deleteRelationPatient(Integer id, User userLogin) {
        if (id == null) {
            return RetUtils.parameterFail();
        }
        RelationPatient patient = RelationPatient.dao.findById(id);
        if (patient == null || !patient.getPatientId().equals(userLogin.getRelation())) {
            return RetUtils.fail("无此其他就诊人");
        }
        return RetUtils.delete(patient.delete());
    }
}