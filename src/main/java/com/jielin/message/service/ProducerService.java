package com.jielin.message.service;

import com.jielin.message.dto.ParamDto;
import com.jielin.message.util.MessageSendAsync;
import com.jielin.message.util.constant.MsgConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * rabbitMq生产者,测试通过生产者生成消息
 *
 * @author yxl
 */
@Service
public class ProducerService {

    @Autowired
    private MessageSendAsync messageSendAsync;

    public void send(ParamDto paramDto) {
        messageSendAsync.sendMsg("", MsgConstant.PUSH_MSG, paramDto);
    }
}
