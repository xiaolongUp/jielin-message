package com.jielin.message.service;

import com.google.gson.Gson;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.util.MsgConstant;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * rabbitMq生产者
 *
 * @author yxl
 */
@Service
public class ProducerService {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Autowired
    private Gson gson;

    public void send() {
        Map<String,Object> params = new HashMap<>();
        params.put("customName","yxl");
        params.put("productName","日常清洁");
        params.put("money","100.0");
        params.put("serviceTime","2019-10-15 17:00:00");
        ParamDto paramDto = new ParamDto();
        paramDto.setOperateType(101)
                .setPhoneNumber("18530076638")
                .setParams(params);


        String context = "hello " + new Date();
        System.out.println("Sender : " + context);
        this.rabbitTemplate.convertAndSend(MsgConstant.PUSH_MSG, paramDto);
    }

    public void topicSend1() {
        String context = "hi, i am message 1";
        System.out.println("Sender : " + context);
        this.rabbitTemplate.convertAndSend("exchange", "topic.message", context);
    }

    public void topicSend2() {
        String context = "hi, i am messages 2";
        System.out.println("Sender : " + context);
        this.rabbitTemplate.convertAndSend("exchange", "topic.messages", context);
    }

    public void fanoutsend() {
        String context = "hi, fanout msg ";
        System.out.println("Sender : " + context);
        this.rabbitTemplate.convertAndSend("fanoutExchange", "", context);
    }

}
