package com.maven.user;

import com.maven.common.bean.ValueTextIntBean;

import java.util.ArrayList;
import java.util.List;

public enum SexEnum {
    MALE(1, "男"),
    FEMALE(0, "女"),
    //
    ;

    private Integer value;
    private String text;

    SexEnum(Integer value, String text) {
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

    public static SexEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (SexEnum v : SexEnum.values()) {
            if (v.getValue().equals(value)) {
                return v;
            }
        }
        return null;
    }

    public static List<ValueTextIntBean> getValues() {
        List<ValueTextIntBean> list = new ArrayList<>();
        for (SexEnum v : SexEnum.values()) {
            list.add(new ValueTextIntBean(v.getValue(), v.getText()));
        }
        return list;
    }
}