package com.maven.appointment;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.maven.common.auth.NeedUserType;
import com.maven.common.controller.AppBaseController;
import com.maven.common.model.Appointment;
import com.maven.common.util.Constant;
import com.maven.doctorManage.DepartmentEnum;
import com.maven.doctorManage.UserService;
import com.maven.user.UserTypeEnum;

import java.util.Date;

@NeedUserType(UserTypeEnum.PATIENT)
public class AppAppointmentController extends AppBaseController {
    private static AppointmentService src = AppointmentService.me;

    public void appointment() {
        Integer department = this.getInt("department");
        Integer doctorId = this.getInt("doctor");
        boolean isSelf = this.getBoolean("isSelf", true);
        Integer patientId = this.getInt("patient");
        Date date = this.getDate("date");
        Integer time = this.getInt("time");
        this.renderJson(src.appointment(department, doctorId, isSelf, patientId, date, time, this.getUserLogin()));
    }

    public void getDepatmentSel() {
        this.renderJson(Ret.ok().set("values", DepartmentEnum.getValues()));
    }

    /*根据科室id查询所有医生*/
    public void getDocList() {
        Integer department = this.getInt("department");
        this.renderJson(Ret.ok().set("values", UserService.me.findDoctorByDepartment(department)));
    }

    /*根据医生id和日期获取可以预约的列表*/
    public void getTimeList() {
        Date date = this.getParaToDate("date");
        Integer doctorId = this.getInt("doctor");
        this.renderJson(src.getTimeList(doctorId, date));
    }

    public void relationPatientSel() {
        this.renderJson(Ret.ok().set("values", UserService.me.findRelationPatientSel(this.getUserLogin())));
    }

    /*我的预约记录*/
    public void myAppointmentList() {
        Integer pageSize = this.getInt("pageSize", Constant.DEFAULT_PAGE_SIZE);
        Integer pageNumber = this.getInt("pageNumber", Constant.DEFAULT_PAGE_NUMBER);
        Page<Appointment> page = src.myAppointmentList(pageSize, pageNumber, this.getUserLogin().getRelation());
        this.renderJson(Ret.ok().set("rows", page.getList()).set("total", page.getTotalRow()));
    }
}