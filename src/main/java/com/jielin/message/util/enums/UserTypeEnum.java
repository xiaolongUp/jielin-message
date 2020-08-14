package com.jielin.message.util.enums;

public enum UserTypeEnum {

    PROVIDER("provider","悦姐"),
    CUSTOMER_ORDER("customer_order","订单系统用户");

    //类型
    private String type;

    //描述
    private String desc;

    UserTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
