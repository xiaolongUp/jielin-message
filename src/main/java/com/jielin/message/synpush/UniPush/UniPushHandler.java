package com.jielin.message.synpush.UniPush;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.base.impl.PushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.notify.Notify;
import com.gexin.rp.sdk.dto.GtReq;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.gexin.rp.sdk.template.style.Style0;
import com.jielin.message.config.AppPushConfig;
import com.jielin.message.config.UniPushConfig;
import com.jielin.message.dao.mongo.TemplateDao;
import com.jielin.message.dao.mysql.GtAliasDao;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.po.Template;
import com.jielin.message.synpush.AppMsgPushHandler;
import com.jielin.message.util.TemplateFactory;
import com.jielin.message.util.enums.PushTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

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
    private GtAliasDao gtAliasDao;

    @Autowired
    private TemplateFactory templateFactory;

    private Map<String, IGtPush> IGtPushMap = new HashMap<>();


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
        UniPushConfig.UniPush uniPush = config.getUniPush().getUniPushMap().get(paramDto.getAppType());
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
        TransmissionTemplate template = createTransmissionTemplate(tmp.getTitle(),
                appTemplate, appId, appKey);
        if (!Optional.ofNullable(template).isPresent()) {
            return false;
        }
        // STEP5：定义"AppMessage"类型消息对象,设置推送消息有效期等推送参数
        List<String> appIds = new ArrayList<>();
        appIds.add(appId);
        AppMessage message = new AppMessage();
        message.setData(template);
        message.setAppIdList(appIds);
        message.setOffline(true);
        message.setOfflineExpireTime(24 * 1000 * 3600);  // 时间单位为毫秒

        // STEP6：执行推送
        IGtPush pushClient = this.IGtPushMap.get(paramDto.getAppType());
        if (!Optional.ofNullable(pushClient).isPresent()) {
            return false;
        }
        PushResult result = (PushResult) pushClient.pushMessageToApp(message);
        log.info("app推送结果:{}",result.getResponse().toString());
        //当result为RepeatedContent时，个推不允许15分钟重复发送数据
        return result.getResponse().get("result").equals("ok");
    }

    //推送通知消息给单一用户
    @Override
    public boolean sendPushToSingle(ParamDto paramDto) throws Exception {

        Template tmp = templateDao.
                selectByOperateAndPushType(paramDto.getOperateType(), PushTypeEnum.APP_PUSH.getType());
        if (!Optional.ofNullable(tmp).isPresent()) {
            return false;
        }
        String appTemplate = templateFactory.newTemplate(paramDto, PushTypeEnum.APP_PUSH.getType(), null);
        if (StringUtils.isBlank(appTemplate)) {
            return false;
        }
        UniPushConfig.UniPush uniPush = config.getUniPush().getUniPushMap().get(paramDto.getAppType());
        if (!Optional.ofNullable(uniPush).isPresent()) {
            return false;
        }

        String appId = uniPush.getAppId();
        String appKey = uniPush.getAppKey();
        TransmissionTemplate template = createTransmissionTemplate(tmp.getTitle(),
                appTemplate, appId, appKey);
        if (!Optional.ofNullable(template).isPresent()) {
            return false;
        }
        SingleMessage message = new SingleMessage();
        message.setData(template);
        // 设置消息离线，并设置离线时间
        message.setOffline(true);
        // 离线有效时间，单位为毫秒，可选
        message.setOfflineExpireTime(72 * 3600 * 1000);
        // 单推消息类型
        Target target = new Target();

        target.setAppId(appId);
        String alias = gtAliasDao.selectAliasByPhone(paramDto.getAppType(), paramDto.getPhoneNumber());
        target.setAlias(alias); //别名需要提前绑定
        IGtPush pushClient = this.IGtPushMap.get(paramDto.getAppType());
        if (!Optional.ofNullable(pushClient).isPresent()) {
            return false;
        }
        IPushResult result = pushClient.pushMessageToSingle(message, target);
        log.info("app推送结果:{}",result.getResponse().toString());
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
    private TransmissionTemplate createTransmissionTemplate(String title, String content, String appId, String appKey) {
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        template.setTransmissionContent(content);
        template.setTransmissionType(2);
        Notify notify = new Notify();
        notify.setTitle(title);
        notify.setContent(content);
        String intent = "intent:#Intent;action=android.intent.action.oppopush;launchFlags=0x14000000;component=com.jielin.provider/io.dcloud.PandoraEntry;S.UP-OL-SU=true;S.title=%s;S.content=%s;S.payload=test;end";
        notify.setIntent(String.format(intent, title, content));
        notify.setType(GtReq.NotifyInfo.Type._intent);
        template.set3rdNotifyInfo(notify);//设置第三方通知
        return template;
    }


}
