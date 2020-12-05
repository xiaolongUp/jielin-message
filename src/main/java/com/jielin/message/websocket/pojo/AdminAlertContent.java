package com.jielin.message.websocket.pojo;


import com.jielin.message.util.enums.WsDataEnum;
import lombok.Data;

/**
 * jielin_web_admin系统请假弹出框展示信息
 */
@Data
public class AdminAlertContent {

    private int type = WsDataEnum.ADMIN_DATA_ALERT.getType();

    /**
     * 请假id
     */
    private Integer leaveId;

    /**
     * 申请人
     */
    private String leaveUser;

    /**
     * 请假起始时间
     */
    private String leaveStartTime;

    /**
     * 请假结束时间
     */
    private String leaveEndTime;

    /**
     * 请假理由
     */
    private String leaveReason;

    /**
     * 影响订单数
     */
    private int affectOrders;

    /**
     * 影响周期定
     */
    private int affectOrderPackage;


}
