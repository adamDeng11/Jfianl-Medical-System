package com.maven.user;

import com.jfinal.kit.Ret;
import com.maven.common.auth.NeedUserType;
import com.maven.common.controller.AdminBaseController;
import com.maven.common.model.RelationPatient;
import com.maven.common.model.User;
import com.maven.common.util.Constant;
import com.maven.doctorManage.UserService;
import com.maven.patientCase.CaseService;

import javax.servlet.http.HttpSession;

@NeedUserType(UserTypeEnum.PATIENT)
public class AppUserInfoController extends AdminBaseController {
    private static UserService src = UserService.me;

    /*修改用户密码，app端*/
    public void modifyPassword() {
        User user = this.getUserLogin();
        String password = this.getPara("password");
        String confirmPassword = this.getPara("confirm_password");
        String oldPassword = this.getPara("old_password");
        Ret ret = src.modifyPassword(user, oldPassword, password, confirmPassword);
        if (ret.isOk()) {
            HttpSession session = this.getSession(true);
            session.invalidate();
        }

        this.renderJson(ret);
    }

    public void relationPatientList() {
        Integer pageSize = this.getInt("pageSize", Constant.DEFAULT_PAGE_SIZE);
        Integer pageNumber = this.getInt("pageNumber", Constant.DEFAULT_PAGE_NUMBER);
        this.renderJson(src.findMyRelationPatients(pageNumber, pageSize, this.getUserLogin().getRelation()));
    }

    public void saveRelationPatient() {
        RelationPatient patient = this.getModel(RelationPatient.class, "", true);
        this.renderJson(src.saveRelationPatient(patient, this.getUserLogin()));
    }

    public void caseList() {
        Integer pageSize = this.getInt("pageSize", Constant.DEFAULT_PAGE_SIZE);
        Integer pageNumber = this.getInt("pageNumber", Constant.DEFAULT_PAGE_NUMBER);
        boolean isSelf = this.getBoolean("isSelf", true);
        Integer patient = this.getInt("patient");
        this.renderJson(CaseService.me.findForPatient(pageSize, pageNumber, isSelf, patient, this.getUserLogin()));
    }

    public void deleteRelationPatient() {
        Integer id = this.getInt("id");
        this.renderJson(src.deleteRelationPatient(id, this.getUserLogin()));
    }
}