package com.jielin.message.util.enums;

/**
 * 用户和悦姐需要推送消息的操作类型
 *
 * @author yxl
 */
@SuppressWarnings("unused")
public enum OperateTypeEnum {

    CREATE_ORDER_SUCCESS(101, "系统下单成功"),
    DISPATCH_ORDER_SUCCESS(102, "派单成功"),
    DEPART_ORDER(103, "出发"),
    ORDER_FINISH(104, "订单完成"),
    ASSIST_CREATE_ORDER_SUCCESS(105, "代客下单成功"),
    GROUP_DEPART_ORDER(106, "服务组出发"),
    DISPATCH_ORDER_SUCCESS_CUSTOMER(107, "派单成功，发送短信给用户"),
    REST_CYCLE_ORDER_REMIND(108, "周期订余量不足提醒"),
    CHANGE_SERVICE(109, "切换服务者"),
    CANCEL_CYCLE_SINGLE_ORDER(110, "周期订单子订单取消通知客户"),
    CANCEL_ORDER(111, "取消订单通知客户"),
    CANCEL_ORDER_NOTIFY_CUSTOM_SERVICE(112, "取消订单通知客服"),
    CANCEL_ORDER_NOTIFY_SERVICE(113, "取消订单通知悦姐");

    //操作类型
    private Integer operateType;

    //操作描述
    private String desc;

    OperateTypeEnum(Integer operateType, String desc) {
        this.operateType = operateType;
        this.desc = desc;
    }


    public Integer getOperateType() {
        return operateType;
    }

    public String getDesc() {
        return desc;
    }

    public static String getDescByType(Integer operateType){
        switch (operateType) {
            case 101:
                return OperateTypeEnum.CREATE_ORDER_SUCCESS.getDesc();
            case 102:
                return OperateTypeEnum.DISPATCH_ORDER_SUCCESS.getDesc();
            case 103:
                return OperateTypeEnum.DEPART_ORDER.getDesc();
            case 104:
                return OperateTypeEnum.ORDER_FINISH.getDesc();
            case 105:
                return OperateTypeEnum.ASSIST_CREATE_ORDER_SUCCESS.getDesc();
            case 106:
                return OperateTypeEnum.GROUP_DEPART_ORDER.getDesc();
            case 107:
                return OperateTypeEnum.DISPATCH_ORDER_SUCCESS_CUSTOMER.getDesc();
            case 108:
                return OperateTypeEnum.REST_CYCLE_ORDER_REMIND.getDesc();
            case 109:
                return OperateTypeEnum.CHANGE_SERVICE.getDesc();
            case 110:
                return OperateTypeEnum.CANCEL_CYCLE_SINGLE_ORDER.getDesc();
            case 111:
                return OperateTypeEnum.CANCEL_ORDER.getDesc();
            case 112:
                return OperateTypeEnum.CANCEL_ORDER_NOTIFY_CUSTOM_SERVICE.getDesc();
            case 113:
                return OperateTypeEnum.CANCEL_ORDER_NOTIFY_SERVICE.getDesc();
            default:
                throw new IllegalArgumentException("错误参数");
        }
    }

    public static OperateTypeEnum valueOf(Integer operateType) {
        switch (operateType) {
            case 101:
                return OperateTypeEnum.CREATE_ORDER_SUCCESS;
            case 102:
                return OperateTypeEnum.DISPATCH_ORDER_SUCCESS;
            case 103:
                return OperateTypeEnum.DEPART_ORDER;
            case 104:
                return OperateTypeEnum.ORDER_FINISH;
            case 105:
                return OperateTypeEnum.ASSIST_CREATE_ORDER_SUCCESS;
            case 106:
                return OperateTypeEnum.GROUP_DEPART_ORDER;
            case 107:
                return OperateTypeEnum.DISPATCH_ORDER_SUCCESS_CUSTOMER;
            case 108:
                return OperateTypeEnum.REST_CYCLE_ORDER_REMIND;
            case 109:
                return OperateTypeEnum.CHANGE_SERVICE;
            case 110:
                return OperateTypeEnum.CANCEL_CYCLE_SINGLE_ORDER;
            case 111:
                return OperateTypeEnum.CANCEL_ORDER;
            case 112:
                return OperateTypeEnum.CANCEL_ORDER_NOTIFY_CUSTOM_SERVICE;
            case 113:
                return OperateTypeEnum.CANCEL_ORDER_NOTIFY_SERVICE;
            default:
                throw new IllegalArgumentException("错误参数");
        }
    }

}
