package com.jielin.message.util.enums;

/**
 * 推送的类型枚举
 *
 * @author yxl
 */
public enum PushTypeEnum {

    NO_PUSH(0, "不推送"),
    SMS_PUSH(1, "短信推送"),
    WX_GZH_PUSH(2, "微信公众号推送"),
    APP_PUSH(3, "app推送"),
    WX_MP_PUSH(4, "微信小程序推送"),
    DING_PUSH(5, "钉钉推送"),
    SYSTEM_PUSH(6, "系统内部推送"),
    WEBSOCKET_PUSH(7, "websocket推送"),
    Mail_PUSH(8, "邮件推送");

    //类型
    private int type;

    //描述
    private String desc;

    PushTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public static PushTypeEnum valueOf(Integer pushType) {
        for (PushTypeEnum value : PushTypeEnum.values()) {
            if (value.getType() == pushType) {
                return value;
            }
        }
        throw new IllegalArgumentException("错误参数");
    }
}
