package com.jielin.message.util.enums;

/**
 * 推送的类型枚举
 *
 * @author yxl
 */
public enum PushTypeEnum {

    NO_PUSH(0, "不推送"),
    SMS_PUSH(1, "短信推送"),
    WX_NP_PUSH(2, "微信公众号推送"),
    APP_PUSH(3, "app推送"),
    WX_MP_PUSH(4, "微信小程序推送"),
    DING_PUSH(5, "钉钉推送");

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
        switch (pushType) {
            case 0:
                return PushTypeEnum.NO_PUSH;
            case 1:
                return PushTypeEnum.SMS_PUSH;
            case 2:
                return PushTypeEnum.WX_NP_PUSH;
            case 3:
                return PushTypeEnum.APP_PUSH;
            case 4:
                return PushTypeEnum.WX_MP_PUSH;
            case 5:
                return PushTypeEnum.DING_PUSH;
            default:
                throw new IllegalArgumentException("错误参数");
        }
    }
}
