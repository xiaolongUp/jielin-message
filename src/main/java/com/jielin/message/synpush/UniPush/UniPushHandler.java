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
import com.jielin.message.dao.mysql.AppTemplateDao;
import com.jielin.message.dao.mysql.GtAliasDao;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.po.AppTemplatePo;
import com.jielin.message.synpush.AppMsgPushHandler;
import com.jielin.message.util.MsgConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private AppTemplateDao appTemplateDao;

    @Autowired
    private GtAliasDao gtAliasDao;

    private IGtPush providerClient;

    private IGtPush customerClient;

    // STEP1：获取应用基本信息
    @PostConstruct
    private void init() {
        this.providerClient = new IGtPush(config.getUniPush().getProviderAppKey(), config.getUniPush().getProviderMasterSecret());
        this.customerClient = new IGtPush(config.getUniPush().getCustomerAppKey(), config.getUniPush().getCustomerMasterSecret());
    }

    // 推送通知消息给所有的用户
    @Override
    public boolean sendPushAll(ParamDto paramDto) throws Exception {
        AppTemplatePo templatePo = appTemplateDao.selectByType(paramDto.getOperateType());
        String appId = config.getUniPush().getAppId(paramDto.getAppType());
        if (StringUtils.isBlank(appId)) {
            return false;
        }
        String appKey = config.getUniPush().getAppKey(paramDto.getAppType());
        if (StringUtils.isBlank(appKey)) {
            return false;
        }
        String content = String.format(templatePo.getContent(), paramDto.getParams().toArray(new String[paramDto.getParams().size()]));
        TransmissionTemplate template = createTransmissionTemplate(templatePo.getTitle(),
                content, appId, appKey);
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
        IGtPush pushClient = getPushClient(paramDto.getAppType());
        if (!Optional.ofNullable(pushClient).isPresent()) {
            return false;
        }
        PushResult result = (PushResult) pushClient.pushMessageToApp(message);
        log.info(result.getResponse().toString());
        return result.getResponse().get("result").equals("ok");
    }

    //推送通知消息给单一用户
    @Override
    public boolean sendPushToSingle(ParamDto paramDto) throws Exception {

        AppTemplatePo templatePo = appTemplateDao.selectByType(paramDto.getOperateType());
        String content = String.format(templatePo.getContent(), paramDto.getParams().toArray(new String[paramDto.getParams().size()]));
        /*NotificationTemplate template = createNoticeTemplate(templatePo.getTitle(),
                content);*/
        String appId = config.getUniPush().getAppId(paramDto.getAppType());
        if (StringUtils.isBlank(appId)) {
            return false;
        }
        String appKey = config.getUniPush().getAppKey(paramDto.getAppType());
        if (StringUtils.isBlank(appKey)) {
            return false;
        }
        TransmissionTemplate template = createTransmissionTemplate(templatePo.getTitle(),
                content, appId, appKey);
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
        IGtPush pushClient = getPushClient(paramDto.getAppType());
        if (!Optional.ofNullable(pushClient).isPresent()) {
            return false;
        }
        IPushResult result = pushClient.pushMessageToSingle(message, target);

        return result.getResponse().get("result").equals("ok");

    }

    private NotificationTemplate createNoticeTemplate(String title, String text,String appId,String appKey) {
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
    private TransmissionTemplate createTransmissionTemplate(String title, String text,String appId,String appKey) {
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        template.setTransmissionContent(text);
        template.setTransmissionType(2);
        Notify notify = new Notify();
        notify.setTitle("测试标题");
        notify.setContent("测试内容");
        //notify.setIntent("intent:#Intent;launchFlags=0x10000000;package=com.pp.yl;component=com.getui.demo/com.getui.demo.MainActivity;i.parm1=12;end");
        notify.setIntent("intent:#Intent;action=android.intent.action.oppopush;launchFlags=0x14000000;component=com.jielin.provider/io.dcloud.PandoraEntry;S.UP-OL-SU=true;S.title=测试标题;S.content=测试内容;S.payload=test;end");
        notify.setType(GtReq.NotifyInfo.Type._intent);
        // notify.setUrl("https://dev.getui.com/");
        //notify.setType(Type._url);
        template.set3rdNotifyInfo(notify);//设置第三方通知
        return template;
    }

    /**
     * 根据app类型判断使用哪个个推的pushClient
     *
     * @param type app类型
     * @return 个推pushClient
     */
    private IGtPush getPushClient(String type) {
        switch (type) {
            case MsgConstant.CUSTOMER_APP:
                return this.customerClient;
            case MsgConstant.PROVIDER_APP:
                return this.providerClient;
            default:
                return null;
        }
    }

}
