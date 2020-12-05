package com.jielin.message.util.enums;

public enum WsDataEnum {

    ADMIN_DATA_HEADER(0,"后台管理系统头信息"),

    ADMIN_DATA_ALERT(1,"后台管理系统弹出框信息");

    //类型
    private Integer type;

    //描述
    private String desc;

    WsDataEnum(Integer type,String desc){
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
