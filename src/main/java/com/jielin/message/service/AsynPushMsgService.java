package com.jielin.message.service;

import com.google.gson.Gson;
import com.jielin.message.dto.DingParamDto;
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
@Slf4j
public class AsynPushMsgService {

    @Autowired
    private SynMsgPushService synMsgPushService;
    @Autowired
    private DingSynMsgPushService dingSynMsgPushService;
    @Autowired
    private Gson gson;

    //本系统需要投递的数据，直接通过对象投递
    @RabbitHandler
    @RabbitListener(queues = MsgConstant.PUSH_MSG)
    public void process(ParamDto paramDto) {
        //synMsgPushService.push(paramDto);
        log.info("Receiver  : {}", paramDto.toString());
    }

    //所有其他系统投递的数据都为字符转
    @RabbitHandler
    @RabbitListener(queues = MsgConstant.PUSH_MSG)
    public void process(String paramDto) {
        ParamDto param = gson.fromJson(paramDto, ParamDto.class);
        synMsgPushService.push(param);
        log.info("Receiver  : {}", paramDto);
    }

    //钉钉推送的消息队列
    @RabbitHandler
    @RabbitListener(queues = MsgConstant.DING_PUSH_MSG)
    public void dingProcess(String dingParamDto) {
        DingParamDto param = gson.fromJson(dingParamDto, DingParamDto.class);
        dingSynMsgPushService.push(param);
        log.info("Receiver  : {}", dingParamDto);
    }


}
