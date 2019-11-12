package com.jielin.message.service;

import com.google.gson.Gson;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.util.MsgConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 异步的推送消息
 *
 * @author yxl
 */
@Service
@RabbitListener(queues = MsgConstant.PUSH_MSG)
@Slf4j
public class AsynPushMsgService {

    @Autowired
    private SynMsgPushService synMsgPushService;
    @Autowired
    private Gson gson;

    //本系统需要投递的数据，直接通过对象投递
    @RabbitHandler
    public void process(ParamDto paramDto) {
        //synMsgPushService.push(paramDto);
        log.info("Receiver  : {}", paramDto.toString());
    }

    //所有其他系统投递的数据都为字符转
    @RabbitHandler
    public void process(String paramDto) {
        ParamDto param = gson.fromJson(paramDto, ParamDto.class);
        synMsgPushService.push(param);
        log.info("Receiver  : {}", paramDto);
    }

}
