package com.jielin.message.synpush.app;

import com.jielin.message.dto.ParamDto;
import com.jielin.message.po.OperatePo;
import com.jielin.message.synpush.MsgPush;
import com.jielin.message.util.constant.AppMsgConstant;
import com.jielin.message.util.enums.PushTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.jielin.message.util.enums.PushTypeEnum.APP_PUSH;

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
    public boolean pushMsg(ParamDto paramDto, OperatePo operatePo) throws Exception {
        super.setThreadLocalParams(paramDto, operatePo, APP_PUSH);
        if (paramDto.getParams().get(AppMsgConstant.APP_PUSH_TO_ALL) != null
                && paramDto.getParams().get(AppMsgConstant.APP_PUSH_TO_ALL) instanceof Boolean
                && (Boolean) paramDto.getParams().get(AppMsgConstant.APP_PUSH_TO_ALL)
        ) {
            return appMsgPushHandler.sendPushAll(paramDto, operatePo);
        } else {
            return appMsgPushHandler.sendPushToSingle(paramDto, operatePo);
        }
    }

    @Override
    public boolean supports(Integer handlerType) {
        return PushTypeEnum.APP_PUSH.getType() == handlerType;
    }


}
