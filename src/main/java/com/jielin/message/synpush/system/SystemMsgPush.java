package com.jielin.message.synpush.system;

import com.jielin.message.dto.ParamDto;
import com.jielin.message.po.OperatePo;
import com.jielin.message.synpush.MsgPush;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.jielin.message.util.enums.PushTypeEnum.SYSTEM_PUSH;

/**
 * 系统内部消息推送
 */
@Component("systemMsgPush")
@Slf4j
public class SystemMsgPush extends MsgPush {
    @Override
    public boolean pushMsg(ParamDto paramDto, OperatePo operatePo) throws Exception {
        super.insertMsgSendLog(paramDto, operatePo.getOperateName(), SYSTEM_PUSH, true, "消息内部推送！");
        return false;
    }

    @Override
    public boolean supports(Integer handlerType) {
        return SYSTEM_PUSH.getType() == handlerType;
    }
}
