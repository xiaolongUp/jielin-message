package com.jielin.message.synpush.app.Jpush;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jiguang.common.resp.DefaultResult;
import cn.jpush.api.JPushClient;
import cn.jpush.api.device.TagAliasResult;
import com.jielin.message.config.AppPushConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 操作用户标签，别名等
 *
 * @author yxl
 */
@Service
@Slf4j
@SuppressWarnings("unused")
public class JgPushDeviceHandler {

    @Autowired
    private AppPushConfig config;

    private JPushClient jPushClient;

    /*@PostConstruct
    private void init() {
        this.jPushClient = new JPushClient(config.getJgPush().getMasterSecret(), config.getJgPush().getAppKey(), null, ClientConfig.getInstance());
    }*/

    /**
     * 绑定手机号码
     *
     * @param registrationId 注册id
     * @param mobile         手机号码
     */
    public boolean bindMobile(String registrationId, String mobile) throws APIConnectionException, APIRequestException {
        log.info("bindMobile,registrationId[" + registrationId + "],mobile[" + mobile + "]");
        DefaultResult result = jPushClient.bindMobile(registrationId, mobile);
        log.info("result " + result);
        return result.isResultOK();
    }

    /**
     * 根据设备的注册id 更新别名、标签
     *
     * @param registrationId 注册id
     * @param clearAlias     标签
     * @param clearTag       别名
     */
    public boolean updateDeviceTagAlias(String registrationId, boolean clearAlias, boolean clearTag) throws APIConnectionException, APIRequestException {
        log.info("updateDeviceTagAlias,registrationId[" + registrationId + "],clearAlias[" + clearAlias + "],clearTag["
                + clearTag + "]");
        DefaultResult result = jPushClient.updateDeviceTagAlias(registrationId, clearAlias, clearTag);
        log.info("result " + result);
        return result.isResultOK();
    }

    /**
     * 根据注册id，获取tags
     *
     * @param registrationId 手机注册id
     */
    public List<String> getDeviceTags(String registrationId) throws APIConnectionException, APIRequestException {
        log.info("getDeviceTags,registrationId[" + registrationId + "]");
        TagAliasResult deviceTagAlias = jPushClient.getDeviceTagAlias(registrationId);
        List<String> tags = deviceTagAlias.tags;
        log.info("tags " + tags);
        return tags;
    }

    /**
     * 根据注册id，获取alias[一个注册id只能有一个alias]
     *
     * @param registrationId 手机注册id
     */
    public String getDeviceAlias(String registrationId) throws APIConnectionException, APIRequestException {
        log.info("getDeviceAlias,registrationId[" + registrationId + "]");
        TagAliasResult deviceTagAlias = jPushClient.getDeviceTagAlias(registrationId);
        String alias = deviceTagAlias.alias;
        log.info("alias " + alias);
        return alias;
    }

}
