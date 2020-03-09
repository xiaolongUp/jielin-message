package com.jielin.message.util.enums;

public enum DingMsgTypeEnum {

    TEXT("text", "文本"),

    IMAGE("image", "图片"),

    LINK("link", "链接");

    private String type;

    private String desc;

    DingMsgTypeEnum(String type, String desc) {
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
