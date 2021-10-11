package com.maven.login;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Clear;
import com.maven.common.controller.AppBaseController;
import com.maven.common.interceptor.MyInterceptor;
import com.maven.common.model.Patient;
import com.maven.common.model.User;
import com.maven.common.util.Constant;
import com.maven.common.util.RetUtils;
import com.maven.doctorManage.UserService;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpSession;
import java.util.UUID;

@Clear(MyInterceptor.class)
public class AppLoginController extends AppBaseController {
    private static LoginService src = LoginService.me;

    public void doLogin() {
        String account = this.getPara("userName");
        String password = this.getPara("password");

        if (StringUtils.isBlank(account) || StringUtils.isBlank(password)) {
            this.renderJson(RetUtils.fail("账号密码有误!"));
            return;
        }
        User user = src.appLogin(account, password);
        if (user == null) {
            this.renderJson(RetUtils.fail("账号密码有误!"));
            return;
        }
        String token = UUID.randomUUID().toString();
        JSONObject json = new JSONObject();
        json.put("token", token);
        json.put("user", user);
        HttpSession session = this.getSession(true);
        this.getRequest().getCookies();
        session.setAttribute(Constant.TOKEN_KEY, token);
        session.setAttribute(Constant.LOGIN_USER_KEY, user);
        this.renderJson(RetUtils.ok("登录成功").set("token", token).set("user", user));
    }

    public void register() {
        Patient patient = this.getModel(Patient.class, "", true);
        String account = this.getPara("account");
        String password = this.getPara("password");
        this.renderJson(src.register(patient, account, password));
    }

    public void checkAccount() {
        String account = this.getPara("account");
        if (StringUtils.isBlank(account)) {
            this.renderJson(RetUtils.parameterFail());
            return;
        }
        this.renderJson(UserService.me.checkAccount(account));
    }

    public void logout() {
        HttpSession session = this.getSession(true);
        session.invalidate();

        this.redirect("/");
    }
}



