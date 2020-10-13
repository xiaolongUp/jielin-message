package com.jielin.message.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fridujo.rabbitmq.mock.compatibility.MockConnectionFactoryFactory;
import com.google.gson.Gson;
import com.jielin.message.JielinMessageApplication;
import com.jielin.message.dao.mongo.TemplateDao;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.po.OperatePo;
import com.jielin.message.service.ProducerService;
import com.jielin.message.service.SynMsgPushService;
import com.jielin.message.synpush.app.UniPush.GtBindingInfo;
import com.jielin.message.synpush.app.UniPush.UniPushHandler;
import com.jielin.message.util.constant.MsgConstant;
import com.taobao.api.ApiException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {JielinMessageApplication.class})
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

    @Autowired
    private ProducerService producerService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JavaMailSender mailSender;



    /**
     * 发送rabbitMq的消息
     */
    @Test
    public void send() throws InterruptedException {
/*
        {"userId":1652,"platform":0,"operateType":102,"phoneNumber"
            :"18456071819","appType":"provider","params":{"orderType":
            "周期清洁:周期清洁 [1人3小时x1]","orderNo":"DD1911281417453483",
                    "customPhone":"18456071819","serviceT日16:00",
                    "productName":"周期清洁","customAddress":"智慧园商务大楼"}}*/

        Map<String, Object> params = new HashMap<>();
        params.put("orderNo", "DD1911281417453483");
        params.put("productName", "周期清洁123");
        params.put("serviceTime", "11月21日14:30");
        params.put("customAddress", "智慧园商务大楼131312312");
        params.put("orderType", "周期清洁:周期清洁 [1人3小时x1]");
        params.put("customPhone", "18530076638");
        ParamDto paramDto = new ParamDto();
        paramDto.setOperateType(102)
                .setUserId(1652)
                .setPhoneNumber("18530076638")
                .setPlatform(0)
                .setUserType("provider")
                .setParams(params);

        rabbitTemplate.convertAndSend(MsgConstant.PUSH_MSG, paramDto);
        //synMsgPushService.push(paramDto);

    }

    /**
     * 插入模版数据
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
        Map<String, Object> map = new HashMap<>();
        map.put("orderNo", "DD1911211725366192");
        map.put("serviceName", "杨晓龙123");
        map.put("serviceTime", "11月22日");
        dto.setPlatform(0)
                .setUserId(6587)
                .setPlatform(0)
                .setOperateType(113)
                .setPhoneNumber("15546049601")
                .setUserType("provider")
                .setParams(map);
        uniPushHandler.sendPushAll(dto, new OperatePo());
    }


    /**
     * 钉钉发送消息测试
     */
    @Test
    public void sendDing() throws ApiException {

        ParamDto paramDto = new ParamDto();
        paramDto.setOperateType(127)
                .setUserId(1652)
                .setPhoneNumber("18530076638")
                .setPlatform(0)
                .setUserType("provider");
        /*paramDto.getDingMsg()
                .setDingMsgType("text")
                .setDingMsgContent("大河哈刚发的户外去哦大1234567890");*/
        rabbitTemplate.convertAndSend(MsgConstant.PUSH_MSG, paramDto);
    }

    /**
     * 获取个推绑定的用户信息
     */
    @Test
    public void gtBindingInfo() {
        RestTemplate restTemplate = new RestTemplate();
        GtBindingInfo userInfo = restTemplate.getForObject("https://test.yueguanjia.com/jielin-web/thirdApp/custom/gtBinding?appType=customer_order&phone=18456071819", GtBindingInfo.class);

        if (userInfo != null) {
            System.out.println(userInfo.getCid());
        }
    }

    /**
     * 发送邮件测试
     */
    @Test
    public void sendSimpleMail() throws Exception {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("ping@yueguanjia.com");
        message.setTo("xiaolong.yang@yueguanjia.com");
        message.setSubject("主题：简单邮件");
        message.setText("测试邮件内容");

        mailSender.send(message);
    }
}
