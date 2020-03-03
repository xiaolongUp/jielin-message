package com.jielin.message.synpush.app;

import com.jielin.message.dto.ParamDto;
import com.jielin.message.synpush.MsgPush;
import com.jielin.message.util.enums.PushTypeEnum;
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

    @Override
    public boolean supports(Integer handlerType) {
        return PushTypeEnum.APP_PUSH.getType() == handlerType;
    }


}
