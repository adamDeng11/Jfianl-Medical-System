package com.maven.common.controller;

import com.jfinal.core.Controller;
import com.maven.common.model.User;
import com.maven.common.util.Constant;

import javax.servlet.http.HttpSession;

public abstract class AppBaseController extends Controller {
    protected User getUserLogin() {
        HttpSession session = this.getSession(true);
        User user = (User) session.getAttribute(Constant.LOGIN_USER_KEY);
        return user;
    }
}