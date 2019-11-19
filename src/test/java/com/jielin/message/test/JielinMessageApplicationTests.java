package com.jielin.message.test;

import com.github.fridujo.rabbitmq.mock.compatibility.MockConnectionFactoryFactory;
import com.google.gson.Gson;
import com.jielin.message.JielinMessageApplication;
import com.jielin.message.dao.mongo.TemplateDao;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.po.Template;
import com.jielin.message.util.MsgConstant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {JielinMessageApplication.class })
@ActiveProfiles("test")
public class JielinMessageApplicationTests {

    @Autowired
    private ApplicationEventPublisher publisher;

    @Bean
    public ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory(MockConnectionFactoryFactory.build());
    }

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Autowired
    private TemplateDao templateDao;

    @Autowired
    private Gson gson;

    /**
     * 发送rabbitMq的消息
     */
    @Test
    public void send() {
        Map<String,Object> params = new HashMap<>();
        params.put("orderNo","1234567");
        params.put("orderType","清洁");
        params.put("productName","日常清洁");
        params.put("serviceTime","2019-10-15 17:00:00");
        params.put("customAddress","智慧园商务大楼七楼");
        ParamDto paramDto = new ParamDto();
        paramDto.setOperateType(101)
                .setUserId(3475)
                .setPhoneNumber("17621158024")
                .setPlatform(0)
                .setAppType("provider")
                .setParams(params);


        String context = "hello " + new Date();
        System.out.println("Sender : " + context);
        this.rabbitTemplate.convertAndSend(MsgConstant.PUSH_MSG, gson.toJson(paramDto));
    }

    /**
     *  插入模版数据
     */
    @Test
    public void insert() {
        Template template = new Template();
        Map<String, String> params = new HashMap<>();
        params.put("keyword1", "customName");
        params.put("keyword2", "productName");
        params.put("keyword3", "money");
        params.put("keyword4", "serviceTime");
        template.setTmpId("103050")
                .setTmpName("订单创建成功短信模板")
                .setOperateType(101).setOptionType(1)
                .setFirst("尊敬的客户，您的服务购买成功")
                .setParamMap(params)
                .setRemark("请您在家等候，感谢配合。联络请致电400-0022-699")
                .setCreateTime(new Date())
                .setUpdateTime(new Date())
                .setEnable(true);
        templateDao.insert(template);
    }

}
