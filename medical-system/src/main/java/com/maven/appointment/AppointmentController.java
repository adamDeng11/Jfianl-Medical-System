package com.maven.appointment;

import com.maven.common.auth.NeedUserType;
import com.maven.common.controller.AdminBaseController;
import com.maven.patientCase.CaseService;
import com.maven.user.UserTypeEnum;

/**
 * 预约后台管理
 */
@NeedUserType(UserTypeEnum.DOCTOR)
public class AppointmentController extends AdminBaseController {
    private static AppointmentService src = AppointmentService.me;

    /*预约列表页面*/
    public void index() {
        this.render("appointment.html");
    }

    public void list() {
        Integer pageSize = this.getParaToInt("limit", 10);
        Integer pageNumber = this.getParaToInt("offset", 0) / pageSize + 1;
        String name = this.getPara("name");
        this.renderJson(src.listAll(pageSize, pageNumber, name, this.getUserLogin()));
    }

    /*根据就诊记录id查询历史病例，支持分页*/
    public void caseList() {
        Integer pageSize = this.getParaToInt("limit", 10);
        Integer pageNumber = this.getParaToInt("offset", 0) / pageSize + 1;
        Integer id = this.getParaToInt("id");
        this.renderJson(CaseService.me.findByAppointment(id, pageSize, pageNumber, this.getUserLogin()));
    }

    /*提交病例*/
    public void addCase() {
        Integer id = this.getParaToInt("id");
        String content = this.getPara("content");
        this.renderJson(CaseService.me.saveForAppointment(id, content, this.getUserLogin()));
    }
}