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
import com.jielin.message.config.UniPushConfig;
import com.jielin.message.dao.mongo.MessageSendLogDao;
import com.jielin.message.dao.mongo.TemplateDao;
import com.jielin.message.dao.mysql.MsgThirdDao;
import com.jielin.message.dto.ParamDto;
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
import java.net.URISyntaxException;
import java.util.*;

import static com.jielin.message.util.enums.PushTypeEnum.APP_PUSH;

/**
 * 个推推送的client
 *
 * @author yxl
 */
@Slf4j
public class UniPushHandler implements AppMsgPushHandler<GtBindingInfo> {

    @Autowired
    private AppPushConfig config;

    @Autowired
    private TemplateDao templateDao;

    @Autowired
    private TemplateFactory templateFactory;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, IGtPush> IGtPushMap = new HashMap<>();

    @Autowired
    private MessageSendLogDao messageSendLogDao;

    @Autowired
    private Gson gson;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MsgThirdDao thirdDao;

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
    public boolean sendPushAll(ParamDto paramDto, OperatePo operatePo) throws Exception {
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
        String appTemplate = templateFactory.newTemplate(paramDto, PushTypeEnum.APP_PUSH.getType(), null);
        if (StringUtils.isBlank(appTemplate)) {
            log.info("生成的空模版：{}", appTemplate);
            return false;
        }
        if (StringUtils.isBlank(tmp.getTitle())) {
            return false;
        }
        TransmissionTemplate template = createTransmissionTemplate(tmp.getTitle(),
                appTemplate, uniPush);
        // STEP5：定义"AppMessage"类型消息对象,设置推送消息有效期等推送参数
        List<String> appIds = new ArrayList<>();
        appIds.add(uniPush.getAppId());
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
    public boolean sendPushToSingle(ParamDto paramDto, OperatePo operatePo) throws Exception {

        Integer platform = paramDto.getPlatform();

        Template tmp = templateDao.
                selectByOperateAndPushType(paramDto.getOperateType(), PushTypeEnum.APP_PUSH.getType());
        if (!Optional.ofNullable(tmp).isPresent()) {
            insertMsgSendLog(paramDto, operatePo.getOperateName(), false, "app模版不存在！");
            return false;
        }
        String appTemplate = templateFactory.newTemplate(paramDto, PushTypeEnum.APP_PUSH.getType(), null);
        if (StringUtils.isBlank(appTemplate)) {
            insertMsgSendLog(paramDto, operatePo.getOperateName(), false, "生产app模版失败！");
            return false;
        }
        String uniPushName = paramDto.getUserType().concat(PlatformService.platformMap.get(platform).toString());
        UniPushConfig.UniPush uniPush = config.getUniPush().getUniPushMap().get(uniPushName);
        if (!Optional.ofNullable(uniPush).isPresent()) {
            insertMsgSendLog(paramDto, operatePo.getOperateName(), false, "uniPush客户端不存在！");
            return false;
        }

        TransmissionTemplate template = createTransmissionTemplate(tmp.getTitle(),
                appTemplate, uniPush);
        SingleMessage message = new SingleMessage();
        message.setData(template);
        // 设置消息离线，并设置离线时间
        message.setOffline(true);
        // 离线有效时间，单位为毫秒，可选
        message.setOfflineExpireTime(72 * 3600 * 1000);
        // 单推消息类型
        Target target = new Target();

        target.setAppId(uniPush.getAppId());
        //获取绑定的cid信息
        GtBindingInfo bindingInfo = getReceiverBindingInfo(paramDto, operatePo);
        //通过平台类型和推送类型获取调用的接口获取别名
        //target.setAlias("18530076638"); //别名需要提前绑定
        if (bindingInfo == null || StringUtils.isBlank(bindingInfo.getCid())) {
            insertMsgSendLog(paramDto, operatePo.getOperateName(), false, "查询不到对应的cid");
            return false;
        }
        target.setClientId(bindingInfo.getCid());
        IGtPush pushClient = this.IGtPushMap.get(uniPushName);
        if (!Optional.ofNullable(pushClient).isPresent()) {
            insertMsgSendLog(paramDto, operatePo.getOperateName(), false, "个推client不存在！");
            return false;
        }
        IPushResult result = pushClient.pushMessageToSingle(message, target);
        boolean resultOk = result.getResponse().get("result").equals("ok");
        insertMsgSendLog(paramDto, operatePo.getOperateName(), resultOk, gson.toJson(result));
        log.info("correlationId:{},app推送结果:{}", paramDto.getCorrelationId(), result.getResponse().toString());
        return resultOk;
    }

    @Override
    public GtBindingInfo getReceiverBindingInfo(ParamDto paramDto, OperatePo operatePo) throws URISyntaxException {
        //通过平台类型和推送类型获取调用的接口返回用户登录过的别名相关信息
        GtBindingInfo bindingInfo = null;
        MsgThirdPoCriteria criteria = new MsgThirdPoCriteria();
        criteria.createCriteria()
                .andPlatformEqualTo(paramDto.getPlatform())
                .andUserTypeEqualTo(paramDto.getUserType())
                .andPushTypeEqualTo(PushTypeEnum.APP_PUSH.getType());
        List<MsgThirdPo> msgThirdPos = thirdDao.selectByExample(criteria);
        if (CollectionUtils.isEmpty(msgThirdPos)) {
            insertMsgSendLog(paramDto, operatePo.getOperateName(), false, "未配置获取个推cid第三方接口！");
            return null;
        }
        //通过第三方接口查询对应的cid绑定信息
        MsgThirdPo msgThird = msgThirdPos.get(0);
        String authBuilder = new URIBuilder(msgThird.getUrl())
                .addParameter("appType", paramDto.getUserType())
                .addParameter("phone", paramDto.getPhoneNumber())
                .build().toString();
        ResponseEntity<GtBindingInfo> remoteCall = restTemplate.exchange(authBuilder, HttpMethod.resolve(msgThird.getHttpMethod().toUpperCase()), null, GtBindingInfo.class);

        if (remoteCall != null && remoteCall.getStatusCode().equals(HttpStatus.OK) &&
                null != remoteCall.getBody()) {
            bindingInfo = remoteCall.getBody();
        }
        //当调用接口没有发生异常且接口没有返回数据时
        if (StringUtils.isBlank(bindingInfo.getCid())) {
            insertMsgSendLog(paramDto, operatePo.getOperateName(), false, "app别名不存在！");
            return null;
        }
        return bindingInfo;
    }


    /**
     * 创建个推推送的通知模版
     */
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

    /**
     * 创建个推推送的透传消息模版
     */
    private TransmissionTemplate createTransmissionTemplate(String title, String content, UniPushConfig.UniPush uniPush) throws JsonProcessingException {
        Map<String, String> map = new HashMap<>();
        map.put("content", content);
        map.put("title", title);
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(uniPush.getAppId());
        template.setAppkey(uniPush.getAppKey());
        template.setTransmissionContent(objectMapper.writeValueAsString(map));
        template.setTransmissionType(2);
        Notify notify = new Notify();
        notify.setTitle(title);
        notify.setContent(content);
        notify.setPayload(objectMapper.writeValueAsString(map));
        String intent = "intent:#Intent;action=android.intent.action.oppopush;launchFlags=0x14000000;component=" + uniPush.getApplicationId() + "/io.dcloud.PandoraEntry;S.UP-OL-SU=true;S.title=%s;S.content=%s;S.payload=test;end";
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

    /**
     * 推送结果日志
     */
    private void insertMsgSendLog(ParamDto paramDto, String operateType, Boolean result, String msg) {
        messageSendLogDao.insert(new MessageSendLog(paramDto, operateType, APP_PUSH.getType(), APP_PUSH.getDesc(), result, msg));
    }
}
