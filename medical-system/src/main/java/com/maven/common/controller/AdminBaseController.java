package com.maven.common.controller;

import com.jfinal.core.Controller;
import com.maven.common.model.User;
import com.maven.common.util.Constant;

public abstract class AdminBaseController extends Controller {
    protected User getUserLogin() {
        User user = this.getSessionAttr(Constant.LOGIN_USER_KEY);
        return user;
    }
}