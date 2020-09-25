package com.jielin.message.util.enums;

public enum SendMsgResultEnum {
    SENDING(0, "投递中"),
    SUCCESS(1, "消费成功"),
    ENQUEUE_FAIL(2, "投递失败"),
    CONSUME_FAIL(3, "消费失败");

    private Integer status;

    private String desc;

    SendMsgResultEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }


    public String getDesc() {
        return desc;
    }

}
