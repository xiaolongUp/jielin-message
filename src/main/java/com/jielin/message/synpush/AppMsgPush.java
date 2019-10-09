package com.jielin.message.synpush;

import com.jielin.message.dto.ParamDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * app推送，采用极光推送实现
 *
 * @author yxl
 */
@Component("appMsgPush")
@Slf4j
public class AppMsgPush extends MsgPush {

    @Autowired
    private AppMsgPushHandler appMsgPushHandler;

    @Override
    public boolean pushMsg(ParamDto paramDto) throws Exception {
        return appMsgPushHandler.sendPushToSingle(paramDto);
    }


}
