package com.jielin.message.service;

import com.google.gson.Gson;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.util.MsgConstant;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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
        List<String> params = new ArrayList<>();
        params.add("test");
        params.add("1111");
        ParamDto paramDto = new ParamDto();
        paramDto.setOperateType(101)
                .setPhoneNumber("18530076638")
                .setParams(params);
        paramDto.getOrderMsg().setOrderNo("123456")
                .setProductName("日常清洁")
                .setCustomName("yxl")
                .setCustomPhone("18530076638")
                .setServiceTime("2019-08-08 08:08:08")
                .setServiceUserName("")
                .setMoney(100.00)
                .setGender("悦姐");


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
