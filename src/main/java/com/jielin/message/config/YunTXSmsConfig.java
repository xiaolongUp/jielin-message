package com.jielin.message.config;

import com.jielin.message.dto.SmsBean;
import com.jielin.message.util.sms.CCPRestSmsSDK;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;

/**
 * 短信发送配置
 */

@Configuration
@ConfigurationProperties("ytxsms")
@Getter
@Setter
public class YunTXSmsConfig {

    private String serverIp;
    private String serverPort;
    private String accountSid;
    private String accountToken;
    private String appId;

    private CCPRestSmsSDK ccpRestSmsSDK;

    @PostConstruct
    public void init() {
        ccpRestSmsSDK = new CCPRestSmsSDK();
        ccpRestSmsSDK.init(serverIp, serverPort);
        ccpRestSmsSDK.setAccount(accountSid, accountToken);
        ccpRestSmsSDK.setAppId(appId);
    }

    /**
     * 发送指定短信
     *
     * @param phoneNumber 客户手机号码
     * @param tplNumber   模板ID号码
     * @param attributes  发送属性值
     */
    public SmsBean sendCommonSMS(String phoneNumber, String tplNumber, String[] attributes) {
        HashMap<String, Object> map = ccpRestSmsSDK.sendTemplateSMS(phoneNumber, tplNumber, attributes);
        return _callbackHandler(map);
    }

    /**
     * SMS服务器返回结果处理函数
     */
    private SmsBean _callbackHandler(HashMap<String, Object> map) {
        SmsBean bean = new SmsBean();
        if ("000000".equals(map.get("statusCode"))) {
            HashMap<String, Object> data = (HashMap<String, Object>) map.get("data");
            data = (HashMap<String, Object>) data.get("templateSMS");
            bean.setIsSuccess(true);
            bean.setMessage(data.get("smsMessageSid").toString());
        } else {
            bean.setIsSuccess(false);
            bean.setMessage(map.get("statusMsg").toString());
        }

        return bean;
    }

}
