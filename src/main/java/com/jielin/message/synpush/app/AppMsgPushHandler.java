package com.jielin.message.synpush.app;

import com.jielin.message.dto.ParamDto;

/**
 * app推送处理的handler接口
 *
 * @author yxl
 */
public interface AppMsgPushHandler {

    // 推送消息给所有的用户
    public boolean sendPushAll(ParamDto paramDto) throws Exception;

    //推送通知给单一用户
    public boolean sendPushToSingle(ParamDto paramDto) throws Exception;
}
