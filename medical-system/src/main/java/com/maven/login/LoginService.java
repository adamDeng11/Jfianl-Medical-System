package com.maven.login;

import com.jfinal.aop.Aop;
import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.maven.common.model.Doctor;
import com.maven.common.model.Patient;
import com.maven.common.model.User;
import com.maven.common.util.RetUtils;
import com.maven.doctorManage.UserService;
import com.maven.user.SexEnum;
import com.maven.user.UserTypeEnum;
import org.apache.commons.lang3.StringUtils;

public class LoginService {
    public static final LoginService me = Aop.get(LoginService.class);
    private User dao = User.dao;

    public User appLogin(String account, String password) {
        SqlPara sqlPara = new SqlPara();
        sqlPara.setSql("select * from t_user where account = ? and password = ? and user_type = ?")
                .addPara(account)
                .addPara(password)
                .addPara(UserTypeEnum.PATIENT.getValue());
        User user = dao.findFirst(sqlPara);
        if (user != null) {
            Patient patient = Patient.dao.findById(user.getRelation());
            int age = UserService.me.getAge(patient.getBirthday());
            patient.put("age", age);
            user.put("relation_info", patient);
        }
        return user;
    }

    public User login(String account, String password) {
        SqlPara sqlPara = new SqlPara();
        sqlPara.setSql("select * from t_user where account = ? and password = ? and user_type != ?")
                .addPara(account)
                .addPara(password)
                .addPara(UserTypeEnum.PATIENT.getValue());
        User user = dao.findFirst(sqlPara);
        if (user != null) {
            switch (UserTypeEnum.getByValue(user.getUserType())) {
                case DOCTOR:
                    Doctor doctor = Doctor.dao.findById(user.getRelation());
                    user.put("relation_info", doctor);
                    break;
                default:
                    user.put("relation_info", null);
                    break;
            }
        }
        return user;
    }

    @Before(Tx.class)
    public Ret register(Patient patient, String account, String password) {
        SexEnum sexEnum = SexEnum.getByValue(patient.getSex());
        if (StringUtils.isBlank(account) || StringUtils.isBlank(password) || sexEnum == null || patient.getBirthday() == null
                || StringUtils.isBlank(patient.getIdNum()) || StringUtils.isBlank(patient.getName()) || StringUtils.isBlank(patient.getPhone())) {
            return RetUtils.parameterFail();
        }
        Ret ret = UserService.me.checkAccount(account);
        if (ret.isFail()) {
            return ret;
        }
        if (!patient.save()) {
            return RetUtils.fail("注册失败");
        }
        User user = new User();
        user.setName(patient.getName());
        user.setSex(patient.getSex());
        user.setAccount(account);
        user.setPassword(password);
        user.setUserType(UserTypeEnum.PATIENT.getValue());
        user.setRelation(patient.getId());
        return user.save() ? RetUtils.ok("注册成功") : RetUtils.fail("注册失败");
    }
}