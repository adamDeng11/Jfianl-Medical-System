package com.maven.login;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;


public class LoginValidator extends Validator {
    protected void validate(Controller c) {
        //有一个错语提示,立即返回错误提示
        setShortCircuit(true);
        validateCaptcha("captcha", "captchaMsg", "验证码不正确");
    }

    protected void handleError(Controller c) {
        c.renderJson();
    }
}
