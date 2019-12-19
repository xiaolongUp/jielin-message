package com.jielin.message.test;

import com.github.fridujo.rabbitmq.mock.compatibility.MockConnectionFactoryFactory;
import com.google.gson.Gson;
import com.jielin.message.JielinMessageApplication;
import com.jielin.message.dao.mongo.TemplateDao;
import com.jielin.message.dto.DingParamDto;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.service.SynMsgPushService;
import com.jielin.message.synpush.UniPush.UniPushHandler;
import com.jielin.message.util.MsgConstant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {JielinMessageApplication.class })
@ActiveProfiles("test")
public class JielinMessageApplicationTests {

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private UniPushHandler uniPushHandler;

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

    @Autowired
    private SynMsgPushService synMsgPushService;

    /**
     * 发送rabbitMq的消息
     */
    @Test
    public void send() {
/*
        {"userId":1652,"platform":0,"operateType":102,"phoneNumber"
            :"18456071819","appType":"provider","params":{"orderType":
            "周期清洁:周期清洁 [1人3小时x1]","orderNo":"DD1911281417453483",
                    "customPhone":"18456071819","serviceT日16:00",
                    "productName":"周期清洁","customAddress":"智慧园商务大楼"}}*/

        Map<String,Object> params = new HashMap<>();
        params.put("orderNo","DD1911281417453483");
        params.put("productName","周期清洁123");
        params.put("serviceTime","11月21日14:30");
        params.put("customAddress","智慧园商务大楼131312312");
        params.put("orderType","周期清洁:周期清洁 [1人3小时x1]");
        params.put("customPhone","18530076638");
        ParamDto paramDto = new ParamDto();
        paramDto.setOperateType(102)
                .setUserId(1652)
                .setPhoneNumber("18530076638")
                .setPlatform(0)
                .setAppType("provider")
                .setParams(params);


        String context = "hello " + new Date();
        System.out.println("Sender : " + context);
        //synMsgPushService.push(paramDto);
        this.rabbitTemplate.convertAndSend(MsgConstant.PUSH_MSG, gson.toJson(paramDto));
    }

    /**
     * 钉钉发送消息测试
     */
    @Test
    public void sendDing() {

        DingParamDto paramDto = new DingParamDto();
        paramDto.setUserId("222311554926292494").setDingMsgContent("987654321");
        this.rabbitTemplate.convertAndSend(MsgConstant.DING_PUSH_MSG, gson.toJson(paramDto));
    }


    /**
     *  插入模版数据
     */
    //@Test
    /*public void insert() {
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
    }*/

    @Test
    public void sendToUniApp() throws Exception {

        ParamDto dto = new ParamDto();
        Map<String,Object> map = new HashMap<>();
        map.put("orderNo","DD1911211725366192");
        map.put("serviceName","杨晓龙123");
        map.put("serviceTime","11月22日");
        dto.setPlatform(0)
                .setUserId(6587)
                .setPlatform(0)
                .setOperateType(113)
                .setPhoneNumber("15546049601")
                .setAppType("provider")
                .setParams(map);
        uniPushHandler.sendPushAll(dto);
    }


}
