package com.maven.user;

import com.jfinal.core.ActionKey;
import com.jfinal.kit.Ret;
import com.maven.common.auth.NeedUserType;
import com.maven.common.controller.AdminBaseController;
import com.maven.common.model.User;
import com.maven.doctorManage.UserService;

public class UserInfoController extends AdminBaseController {
    private static UserService src = UserService.me;

    @ActionKey("/modifyPassword")
    @NeedUserType({UserTypeEnum.ADMIN, UserTypeEnum.DOCTOR})
    public void modifyPassword() {
        this.render("modifyPassword.html");
    }

    @NeedUserType({UserTypeEnum.ADMIN, UserTypeEnum.DOCTOR})
    public void doModifyPassword() {
        User user = this.getUserLogin();
        String password = this.getPara("password");
        String oldPassword = this.getPara("old_password");
        String confirmPassword = this.getPara("confirm_password");
        Ret ret = src.modifyPassword(user, oldPassword, password, confirmPassword);
        if (ret.isOk()) {
            this.getSession().invalidate();
        }

        this.renderJson(ret);
    }
}