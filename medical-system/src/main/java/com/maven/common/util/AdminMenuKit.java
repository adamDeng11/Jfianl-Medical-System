package com.maven.common.util;

import com.maven.common.bean.AdminMenu;
import com.maven.user.UserTypeEnum;

import java.util.ArrayList;
import java.util.List;

public class AdminMenuKit {
    private List<AdminMenu> adminMenuList = new ArrayList<>();
    private List<AdminMenu> doctorMenuList = new ArrayList<>();

    public AdminMenuKit() {
        this.addAdminMenuList();
        this.addDoctorMenuList();
    }

    private void addAdminMenuList() {
        adminMenuList.add(new AdminMenu("/doctorManage", "ti-user", "医生管理"));
    }

    private void addDoctorMenuList() {
        doctorMenuList.add(new AdminMenu("/appointment", "ti-bell", "问诊"));
    }

    public List<AdminMenu> getMenuList(Integer userType) {
        UserTypeEnum userTypeEnum = UserTypeEnum.getByValue(userType);
        if (userTypeEnum == null) {
            return new ArrayList<>();
        }
        switch (userTypeEnum) {
            case ADMIN:
                return adminMenuList;
            case DOCTOR:
                return doctorMenuList;
            default:
                return new ArrayList<>();
        }
    }
}
