package com.jielin.message.websocket.pojo;


import com.jielin.message.util.enums.WsDataEnum;
import lombok.Data;

/**
 * jielin_web_admin系统头部展示信息
 */
@Data
public class AdminHeaderContent {

    private int type = WsDataEnum.ADMIN_DATA_HEADER.getType();

    /**
     * 待审批请假数量
     */
    private long leaveWaitApproval;

    /**
     * 今日待排单数量
     */
    private long waitSchedule;

    /**
     * 异常订单数量（溢出订单）
     */
    private long overflowOrder;
}
