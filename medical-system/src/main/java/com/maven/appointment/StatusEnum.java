package com.maven.appointment;

import com.maven.common.bean.ValueTextIntBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 预约状态
 */
public enum StatusEnum {
    WAITING(0, "已预约/待问诊"),
    COMPLETE(1, "已就诊"),
    //
    ;

    private Integer value;
    private String text;

    StatusEnum(Integer value, String text) {
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

    public static StatusEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (StatusEnum v : StatusEnum.values()) {
            if (v.getValue().equals(value)) {
                return v;
            }
        }
        return null;
    }

    public static List<ValueTextIntBean> getValues() {
        List<ValueTextIntBean> list = new ArrayList<>();
        for (StatusEnum v : StatusEnum.values()) {
            list.add(new ValueTextIntBean(v.getValue(), v.getText()));
        }
        return list;
    }
}