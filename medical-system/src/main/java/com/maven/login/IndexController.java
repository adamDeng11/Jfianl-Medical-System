package com.maven.login;

import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.maven.common.controller.AdminBaseController;
import com.maven.common.interceptor.MyInterceptor;

public class IndexController extends AdminBaseController {
    public void index() {
        this.render("index.html");
    }

    @ActionKey("/")
    @Clear(MyInterceptor.class)
    public void redirect() {
        this.redirect("/login");
    }
}