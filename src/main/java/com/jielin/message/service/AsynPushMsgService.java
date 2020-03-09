package com.jielin.message.service;

import com.jielin.message.dto.ParamDto;
import com.jielin.message.util.constant.MsgConstant;
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

    //监听消息队列当中的数据
    @RabbitHandler
    @RabbitListener(queues = MsgConstant.PUSH_MSG)
    public void process(ParamDto paramDto) {
        // 业务处理成功后调用，消息会被确认消费
        //channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        synMsgPushService.push(paramDto);
        log.info("Receiver  : {}", paramDto.toString());
        //业务失败后处理
        //channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
    }

}
