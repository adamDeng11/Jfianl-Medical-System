package com.maven.login;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.Ret;
import com.maven.common.controller.AdminBaseController;
import com.maven.common.interceptor.MyInterceptor;
import com.maven.common.model.User;
import com.maven.common.util.Constant;
import com.maven.common.util.RetUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpSession;

@Clear(MyInterceptor.class)
public class LoginController extends AdminBaseController {
    private static LoginService src = LoginService.me;

    public void index() {
        this.render("login.html");
    }

    public void captcha() {
        this.renderCaptcha();
    }

    @Before(LoginValidator.class)
    public void doLogin() {
        String account = this.getPara("userName");
        String password = this.getPara("password");

        if (StringUtils.isBlank(account) || StringUtils.isBlank(password)) {
            this.renderJson(RetUtils.fail("账号密码有误!"));
            return;
        }
        User user = src.login(account, password);
        if (user == null) {
            this.renderJson(RetUtils.fail("账号密码有误!"));
            return;
        }
        this.setSessionAttr(Constant.LOGIN_USER_KEY, user);
        this.renderJson(Ret.ok());
    }

    @ActionKey("/logout")
    public void logout() {
        HttpSession session = getSession();
        session.invalidate();

        this.redirect("/");
    }
}



