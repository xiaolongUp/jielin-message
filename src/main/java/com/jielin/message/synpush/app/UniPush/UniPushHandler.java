package com.jielin.message.synpush.app.UniPush;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.base.impl.PushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.notify.Notify;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.dto.GtReq;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.gexin.rp.sdk.template.style.Style0;
import com.google.gson.Gson;
import com.jielin.message.config.AppPushConfig;
import com.jielin.message.config.ThirdApiConfig;
import com.jielin.message.config.UniPushConfig;
import com.jielin.message.dao.mongo.MessageSendLogDao;
import com.jielin.message.dao.mongo.ProviderOrderLogDao;
import com.jielin.message.dao.mongo.TemplateDao;
import com.jielin.message.dao.mysql.MsgThirdDao;
import com.jielin.message.dao.mysql.MsgUserDao;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.dto.ResponsePackDto;
import com.jielin.message.po.*;
import com.jielin.message.service.PlatformService;
import com.jielin.message.synpush.app.AppMsgPushHandler;
import com.jielin.message.util.TemplateFactory;
import com.jielin.message.util.enums.PushTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 个推推送的client
 *
 * @author yxl
 */
@Slf4j
public class UniPushHandler implements AppMsgPushHandler {

    @Autowired
    private AppPushConfig config;

    @Autowired
    private TemplateDao templateDao;

    @Autowired
    private TemplateFactory templateFactory;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ProviderOrderLogDao providerOrderLogDao;

    private Map<String, IGtPush> IGtPushMap = new HashMap<>();

    @Autowired
    private MessageSendLogDao messageSendLogDao;

    @Autowired
    private Gson gson;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MsgThirdDao thirdDao;

    @Autowired
    private ThirdApiConfig thirdApiConfig;

    @Autowired
    private MsgUserDao msgUserDao;


    // STEP1：获取应用基本信息
    @PostConstruct
    private void init() {
        //初始化所有的uniapp的client
        for (Map.Entry<String, UniPushConfig.UniPush> uniPushEntry : config.getUniPush().getUniPushMap().entrySet()) {
            IGtPush iGtPush = new IGtPush(uniPushEntry.getValue().getAppKey(), uniPushEntry.getValue().getMasterSecret());
            this.IGtPushMap.put(uniPushEntry.getKey(), iGtPush);
        }
    }

    // 推送通知消息给所有的用户
    @Override
    public boolean sendPushAll(ParamDto paramDto) throws Exception {
        Template tmp = templateDao.
                selectByOperateAndPushType(paramDto.getOperateType(), PushTypeEnum.APP_PUSH.getType());

        String uniPushName = paramDto.getUserType().concat(PlatformService.platformMap.get(paramDto.getPlatform()).toString());
        UniPushConfig.UniPush uniPush = config.getUniPush().getUniPushMap().get(uniPushName);
        if (!Optional.ofNullable(tmp).isPresent()) {
            return false;
        }
        if (!Optional.ofNullable(uniPush).isPresent()) {
            return false;
        }

        String appId = uniPush.getAppId();
        String appKey = uniPush.getAppKey();
        String appTemplate = templateFactory.newTemplate(paramDto, PushTypeEnum.APP_PUSH.getType(), null);
        if (StringUtils.isBlank(appTemplate)) {
            log.info("生成的空模版：{}", appTemplate);
            return false;
        }
        if (StringUtils.isBlank(tmp.getTitle())) {
            return false;
        }
        TransmissionTemplate template = createTransmissionTemplate(tmp.getTitle(),
                appTemplate, appId, appKey);
        // STEP5：定义"AppMessage"类型消息对象,设置推送消息有效期等推送参数
        List<String> appIds = new ArrayList<>();
        appIds.add(appId);
        AppMessage message = new AppMessage();
        message.setData(template);
        message.setAppIdList(appIds);
        message.setOffline(true);
        message.setOfflineExpireTime(24 * 1000 * 3600);  // 时间单位为毫秒

        // STEP6：执行推送
        IGtPush pushClient = this.IGtPushMap.get(uniPushName);
        if (!Optional.ofNullable(pushClient).isPresent()) {
            return false;
        }
        PushResult result = (PushResult) pushClient.pushMessageToApp(message);
        log.info("app推送结果:{}", result.getResponse().toString());
        //当result为RepeatedContent时，个推不允许15分钟重复发送数据
        return result.getResponse().get("result").equals("ok");
    }

    //推送通知消息给单一用户
    @Override
    public boolean sendPushToSingle(ParamDto paramDto) throws Exception {

        String userType = paramDto.getUserType();
        Integer userId = paramDto.getUserId();
        Integer platform = paramDto.getPlatform();

        Template tmp = templateDao.
                selectByOperateAndPushType(paramDto.getOperateType(), PushTypeEnum.APP_PUSH.getType());
        if (!Optional.ofNullable(tmp).isPresent()) {
            return false;
        }
        String appTemplate = templateFactory.newTemplate(paramDto, PushTypeEnum.APP_PUSH.getType(), null);
        if (StringUtils.isBlank(appTemplate)) {
            return false;
        }
        String uniPushName = paramDto.getUserType().concat(PlatformService.platformMap.get(platform).toString());
        UniPushConfig.UniPush uniPush = config.getUniPush().getUniPushMap().get(uniPushName);
        if (!Optional.ofNullable(uniPush).isPresent()) {
            return false;
        }

        String appId = uniPush.getAppId();
        String appKey = uniPush.getAppKey();
        TransmissionTemplate template = createTransmissionTemplate(tmp.getTitle(),
                appTemplate, appId, appKey);
        SingleMessage message = new SingleMessage();
        message.setData(template);
        // 设置消息离线，并设置离线时间
        message.setOffline(true);
        // 离线有效时间，单位为毫秒，可选
        message.setOfflineExpireTime(72 * 3600 * 1000);
        // 单推消息类型
        Target target = new Target();

        target.setAppId(appId);
        //通过平台类型和推送类型获取调用的接口获取别名
        String alias = null;
        MsgThirdPoCriteria criteria = new MsgThirdPoCriteria();
        criteria.createCriteria()
                .andPlatformEqualTo(platform)
                .andUserTypeEqualTo(userType)
                .andPushTypeEqualTo(PushTypeEnum.APP_PUSH.getType());
        List<MsgThirdPo> msgThirdPos = thirdDao.selectByExample(criteria);
        if (CollectionUtils.isEmpty(msgThirdPos)) {
            return false;
        }
        MsgThirdPo msgThird = msgThirdPos.get(0);
        String builder = thirdApiConfig.getJlWebApiUrl() + msgThird.getUrl();
        String authBuilder = new URIBuilder(builder)
                .addParameter("appType", userType)
                .addParameter("phone", paramDto.getPhoneNumber())
                .build().toString();
        ResponseEntity<ResponsePackDto> authResult = null;
        //更新用户所在平台的别名
        boolean hasException = false;
        MsgUserPo msgUserPo =
                msgUserDao.selectByCondition(platform, userType, userId);
        try {
            authResult = restTemplate.exchange(authBuilder, HttpMethod.resolve(msgThird.getHttpMethod().toUpperCase()), null, ResponsePackDto.class);
        } catch (Exception e) {
            //调用接口发生异常时，使用本地存储数据
            hasException = true;
            if (null != msgUserPo && StringUtils.isNotBlank(msgUserPo.getUniappAlias())) {
                alias = msgUserPo.getUniappAlias();
            }
        }

        if (authResult != null && authResult.getStatusCode().equals(HttpStatus.OK) &&
                null != authResult.getBody()) {
            if (null != authResult.getBody().getBody()) {
                alias = (String) authResult.getBody().getBody();
                if (null == msgUserPo) {
                    MsgUserPo record = new MsgUserPo();
                    record.setPlatform(platform);
                    record.setUserType(userType);
                    record.setUserId(userId);
                    record.setUserPhone(paramDto.getPhoneNumber());
                    record.setUniappAlias(alias);
                    msgUserDao.insert(record);
                } else if (StringUtils.isBlank(msgUserPo.getUniappAlias())) {
                    msgUserPo.setUniappAlias(alias);
                    MsgUserPoCriteria example = new MsgUserPoCriteria();
                    example.createCriteria()
                            .andUserIdEqualTo(userId)
                            .andUserTypeEqualTo(userType)
                            .andPlatformEqualTo(platform);
                    msgUserDao.updateByExample(msgUserPo, example);
                }
            }
        }
        //当调用接口没有发生异常且接口没有返回数据时，重试使用本地的存储数据
        if (StringUtils.isBlank(alias) && !hasException) {

            if (null != msgUserPo && StringUtils.isNotBlank(msgUserPo.getUniappAlias())) {
                alias = msgUserPo.getUniappAlias();
            }
        }
        if (StringUtils.isBlank(alias)) {
            return false;
        }
        target.setAlias(alias); //别名需要提前绑定
        IGtPush pushClient = this.IGtPushMap.get(uniPushName);
        if (!Optional.ofNullable(pushClient).isPresent()) {
            return false;
        }
        IPushResult result = pushClient.pushMessageToSingle(message, target);
        ProviderOrderLog logs = new ProviderOrderLog(alias,
                PushTypeEnum.APP_PUSH.getDesc(),
                template.getTransmissionContent());
        providerOrderLogDao.insert(logs);
        messageSendLogDao.insert(new MessageSendLog(paramDto, PushTypeEnum.APP_PUSH.getDesc(), gson.toJson(result)));
        log.info("app推送结果:{}", result.getResponse().toString());
        return result.getResponse().get("result").equals("ok");

    }

    private NotificationTemplate createNoticeTemplate(String title, String text, String appId, String appKey) {
        Style0 style = new Style0();
        // STEP2：设置推送标题、推送内容
        style.setTitle(title);
        style.setText(text);
        style.setLogo("push.png");  // 设置推送图标
        // STEP3：设置响铃、震动等推送效果
        style.setRing(true);  // 设置响铃
        style.setVibrate(true);  // 设置震动


        // STEP4：选择通知模板
        NotificationTemplate template = new NotificationTemplate();

        template.setAppId(appId);
        template.setAppkey(appKey);
        template.setStyle(style);
        template.setTransmissionType(1);  // 透传消息接受方式设置，1：立即启动APP，2：客户端收到消息后需要自行处理
        template.setTransmissionContent("请输入您要透传的内容");
        return template;
    }

    //创建透传消息模版
    private TransmissionTemplate createTransmissionTemplate(String title, String content, String appId, String appKey) throws JsonProcessingException {
        Map<String, String> map = new HashMap<>();
        map.put("content", content);
        map.put("title", title);
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        template.setTransmissionContent(objectMapper.writeValueAsString(map));
        template.setTransmissionType(2);
        Notify notify = new Notify();
        notify.setTitle(title);
        notify.setContent(content);
        notify.setPayload(objectMapper.writeValueAsString(map));
        String intent = "intent:#Intent;action=android.intent.action.oppopush;launchFlags=0x14000000;component=com.jielin.provider/io.dcloud.PandoraEntry;S.UP-OL-SU=true;S.title=%s;S.content=%s;S.payload=test;end";
        notify.setIntent(String.format(intent, title, content));
        notify.setType(GtReq.NotifyInfo.Type._intent);
        template.set3rdNotifyInfo(notify);//设置第三方通知
        //设置ios推送
        APNPayload apnPayload = new APNPayload();
        //推送直接带有透传数据
        apnPayload.setContentAvailable(1);
        apnPayload.setSound("default");
        apnPayload.addCustomMsg("payload", objectMapper.writeValueAsString(map));
        apnPayload.addCustomMsg("title", title);
        apnPayload.addCustomMsg("content", content);
        template.setAPNInfo(apnPayload);
        //log.info("推送给app的消息为:{}", notify.toString());
        return template;
    }


}
