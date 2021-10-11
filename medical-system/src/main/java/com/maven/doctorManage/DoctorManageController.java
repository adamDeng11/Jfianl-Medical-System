package com.maven.doctorManage;

import com.maven.common.auth.NeedUserType;
import com.maven.common.controller.AdminBaseController;
import com.maven.common.model.User;
import com.maven.user.SexEnum;
import com.maven.user.UserTypeEnum;

@NeedUserType(UserTypeEnum.ADMIN)
public class DoctorManageController extends AdminBaseController {
    private static UserService src = UserService.me;

    public void index() {
        this.setAttr("departmentEnum", DepartmentEnum.getValues());
        this.setAttr("sexEnum", SexEnum.getValues());
        this.render("doctorManage.html");
    }

    public void list() {
        Integer pageSize = this.getParaToInt("limit", 10);
        Integer pageNumber = this.getParaToInt("offset", 0) / pageSize + 1;
        this.renderJson(src.list(pageNumber, pageSize));
    }

    public void checkAccount() {
        String account = this.getPara("account");
        this.renderJson(src.checkAccount(account));
    }

    public void save() {
        User model = this.getModel(User.class, "", true);
        Integer department = this.getInt("department");
        String introduction = this.getPara("introduction");
        this.renderJson(src.saveDoctor(model, department, introduction));
    }

    public void update() {
        User model = this.getModel(User.class, "", true);
        Integer department = this.getInt("department");
        String introduction = this.getPara("introduction");
        this.renderJson(src.updateDoctor(model, department, introduction));
    }

    public void delete() {
        Integer id = this.getInt("id");
        this.renderJson(src.deleteDoctor(id));
    }
}