package com.maven.patientCase;

import com.jfinal.aop.Aop;
import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.maven.appointment.StatusEnum;
import com.maven.common.model.Appointment;
import com.maven.common.model.Case;
import com.maven.common.model.RelationPatient;
import com.maven.common.model.User;
import com.maven.common.util.RetUtils;
import com.maven.doctorManage.DepartmentEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

public class CaseService {
    public static final CaseService me = Aop.get(CaseService.class);
    private Case dao = Case.dao;

    /**
     *根据就诊记录id查询历史病例，支持分页
     */
    public Ret findByAppointment(Integer id, Integer pageSize, Integer pageNumber, User userLogin) {
        if (id == null) {
            return RetUtils.parameterFail();
        }
        Appointment appointment = Appointment.dao.findById(id);
        if (appointment == null || !appointment.getDoctorId().equals(userLogin.getRelation())) {
            return RetUtils.fail("无此预约记录");
        }
        return findForPatient(pageSize, pageNumber, appointment.getRealPatientId());
    }

    /**
     *根据病人id查询历史病例，支持分页
     */
    public Ret findForPatient(Integer pageSize, Integer pageNumber, Integer patientId) {
        if (patientId == null) {
            return RetUtils.parameterFail();
        }
        Page<Case> page = dao.paginate(pageNumber, pageSize, "select * ", " from t_case where real_patient_id = ? order by date", patientId);
        List<Case> list = page.getList();
        list.forEach(e -> {
            DepartmentEnum departmentEnum = DepartmentEnum.getByValue(e.getDepartment());
            e.put("department_text", departmentEnum == null ? "" : departmentEnum.getText());
        });
        return Ret.ok().set("rows", list).set("total", page.getTotalRow());
    }

    @Before(Tx.class)
    public Ret saveForAppointment(Integer id, String content, User userLogin) {
        if (id == null || StringUtils.isBlank(content)) {
            return RetUtils.parameterFail();
        }
        Appointment appointment = Appointment.dao.findById(id);
        if (appointment == null || !appointment.getDoctorId().equals(userLogin.getRelation())) {
            return RetUtils.fail("无此预约记录");
        }

        appointment.setStatus(StatusEnum.COMPLETE.getValue());
        if (!appointment.update()) {
            return RetUtils.saveFail();
        }

        Case patientCase = new Case();
        patientCase.setPatientId(appointment.getPatientId());
        patientCase.setContent(content);
        patientCase.setIsSelf(appointment.getIsSelf());
        patientCase.setRealPatientId(appointment.getRealPatientId());
        patientCase.setRealPatientName(appointment.getRealPatientName());
        patientCase.setDepartment(appointment.getDepartment());
        patientCase.setDoctorId(appointment.getDoctorId());
        patientCase.setDoctorName(appointment.getDoctorName());
        patientCase.setDate(new Date());
        return RetUtils.save(patientCase.save());
    }

    public Ret findForPatient(Integer pageSize, Integer pageNumber, boolean isSelf, Integer patientId, User userLogin) {
        if (patientId == null && !isSelf) {
            return RetUtils.parameterFail();
        }
        Integer realPatientId = userLogin.getRelation();
        if (!isSelf) {
            RelationPatient patient = RelationPatient.dao.findById(patientId);
            if (patient == null || !patient.getPatientId().equals(userLogin.getRelation())) {
                return RetUtils.fail("无此其他就诊人");
            }
            realPatientId = patientId;
        }
        return findForPatient(pageSize, pageNumber, realPatientId);
    }
}