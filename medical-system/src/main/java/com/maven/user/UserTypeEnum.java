package com.maven.user;

import com.maven.common.bean.ValueTextIntBean;

import java.util.ArrayList;
import java.util.List;

public enum UserTypeEnum {
    ADMIN(0, "管理员"),
    DOCTOR(1, "医生"),
    PATIENT(2, "患者"),
    //
    ;

    private Integer value;
    private String text;

    UserTypeEnum(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static UserTypeEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (UserTypeEnum v : UserTypeEnum.values()) {
            if (v.getValue().equals(value)) {
                return v;
            }
        }
        return null;
    }

    public static List<ValueTextIntBean> getValues() {
        List<ValueTextIntBean> list = new ArrayList<>();
        for (UserTypeEnum v : UserTypeEnum.values()) {
            list.add(new ValueTextIntBean(v.getValue(), v.getText()));
        }
        return list;
    }
}