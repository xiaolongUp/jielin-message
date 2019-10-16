package com.jielin.message.util.enums;

import com.jielin.message.po.MsgPushPo;

/**
 * 推送的类型枚举
 *
 * @author yxl
 */
@SuppressWarnings("unused")
public enum PushTypeEnum {

    NO_PUSH(0, "不推送"),
    SMS_PUSH(1, "短信推送"),
    WX_NP_PUSH(2, "微信公众推送"),
    APP_PUSH(3, "app推送");

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

    public static String getMsgPush(MsgPushPo msgPushPo) {
        switch (msgPushPo.getOptionValue()) {
            case 1:
                return "smsMsgPush";
            case 2:
                return "wxMsgPush";
            case 3:
                return "appMsgPush";
            default:
                return "";
        }
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
            default:
                throw new IllegalArgumentException("错误参数");
        }
    }
}
