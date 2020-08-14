package com.jielin.message.synpush.app;

import com.jielin.message.dto.ParamDto;
import com.jielin.message.po.OperatePo;

import java.net.URISyntaxException;

/**
 * app推送处理的handler接口
 *
 * @author yxl
 */
public interface AppMsgPushHandler<T> {

    // 推送消息给所有的用户
    public boolean sendPushAll(ParamDto paramDto, OperatePo operatePo) throws Exception;

    //推送通知给单一用户
    public boolean sendPushToSingle(ParamDto paramDto, OperatePo operatePo) throws Exception;

    //获取需要推送消息的接收人的信息
    public T getReceiverBindingInfo(ParamDto paramDto, OperatePo operatePo) throws URISyntaxException;
}
