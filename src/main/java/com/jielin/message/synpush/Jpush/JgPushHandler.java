package com.jielin.message.synpush.Jpush;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.PushPayload;
import com.jielin.message.config.AppPushConfig;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.synpush.AppMsgPushHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 极光推送的客户端
 *
 * @author yxl
 */
@Slf4j
@SuppressWarnings("unused")
public class JgPushHandler implements AppMsgPushHandler {

    @Autowired
    private AppPushConfig config;

    private JPushClient jPushClient;

    /*@PostConstruct
    private void init() {
        this.jPushClient = new JPushClient(config.getJgPush().getMasterSecret(), config.getJgPush().getAppKey(), null, ClientConfig.getInstance());
    }*/

    /**
     * 给所有的平台和用户都推送消息
     */
    @Override
    public boolean sendPushAll(ParamDto paramDto) throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.alertAll("test");
        PushResult result = jPushClient.sendPush(payload);
        return result.isResultOK();
    }

    /**
     * @param phoneNumber 用户手机号
     * @param title       发送的标题
     * @param msgContent  发送消息
     */
    public boolean sendPushMessageWithPhone(String phoneNumber, String title, String msgContent) throws APIConnectionException, APIRequestException {
        //todo
        //查询出数据库用户所有的客户端的别名
        String[] alias = new String[]{};
        PushResult result = jPushClient.sendAndroidMessageWithAlias(title, msgContent, alias);
        PushResult result1 = jPushClient.sendIosMessageWithAlias(title, msgContent, alias);

        return result.isResultOK();
    }

    /**
     * @param paramDto 参数
     */
    @Override
    public boolean sendPushToSingle(ParamDto paramDto) throws APIConnectionException, APIRequestException {
        //todo
        //查询出数据库用户所有的客户端的别名
        String[] alias = new String[]{};
        String title = "test";
        String msgContent = "测试";
        PushResult result = jPushClient.sendAndroidNotificationWithAlias(title, msgContent, null, alias);
        PushResult result1 = jPushClient.sendIosNotificationWithAlias(msgContent, null, alias);

        return result.isResultOK();
    }

}
