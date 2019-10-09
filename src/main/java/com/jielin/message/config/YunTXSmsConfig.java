package com.jielin.message.config;

import com.jielin.message.dto.SmsBean;
import com.jielin.message.util.sms.CCPRestSmsSDK;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;

/**
 * 短信发送配置
 */

@Configuration
@ConfigurationProperties("ytxsms")
@Getter
@Setter
@SuppressWarnings("unused")
public class YunTXSmsConfig {

    private String serverIp;
    private String serverPort;
    private String accountSid;
    private String accountToken;
    private String registerUrl;
    private String appId;
    /**
     * 新用户注册发送短信模板
     */
    private String customerRegisterNotify;
    /**
     * 注册短信验证码模板
     */
    private String registerTpl;
    /**
     * 悦联盟短信验证码模板
     */
    private String registerTp2;
    /**
     * 周期包短信提醒模板
     */
    private String orderPackageRemind;
    /**
     * 订单签单短信模板
     */
    private String orderSignupTpl;
    /**
     * 订单服务者出发短信模板
     */
    private String orderServiceDepartTpl;
    /**
     * 订单服务者组出发短信模板
     */
    private String orderServiceGroupDepartTpl;
    /**
     * 订单派单短信模板
     */
    private String orderDispatchTpl;
    /**
     * 订单创建成功短信模板
     */
    private String orderCreateTpl;
    /**
     * 代客下单成功短信模板
     */
    private String assistOrderCreateTpl;
    /**
     * 切换服务者通知服务者
     */
    private String changeServiceNotifyServiceTpl;
    /**
     * 订单取消通知服务者
     */
    private String orderCancelNotifyServiceTpl;
    /**
     * 订单取消通知客服
     */
    private String orderCancelNotifyCustomServiceTpl;
    /**
     * 订单取消通知客户
     */
    private String orderCancelNotifyCustomerTpl;
    private String customServiceMobilePhone;
    private List<String> customerServicePhones;
    /**
     * 排单后通知客户
     */
    private String orderDispatchNotifyCustomer;
    /**
     * 取消生成的周期订单时发送短信给客户
     */
    private String cycleSingleOrderCancelNotifyCustomer;
    /**
     * 剩余周期包次数提醒
     */
    private String restCycleOrderRemind;
    private CCPRestSmsSDK ccpRestSmsSDK;

    @PostConstruct
    public void init() {
        ccpRestSmsSDK = new CCPRestSmsSDK();
        ccpRestSmsSDK.init(serverIp, serverPort);
        ccpRestSmsSDK.setAccount(accountSid, accountToken);
        ccpRestSmsSDK.setAppId(appId);
    }

    /**
     * 发送验证码
     *
     * @param phoneNumber 客户手机号码
     * @param minute      过期时长
     * @param verify      验证码
     */
    public SmsBean sendVerify(String phoneNumber, String minute, String verify) {
        HashMap<String, Object> map = ccpRestSmsSDK.sendTemplateSMS(phoneNumber, registerTpl, new String[]{verify, minute});
        return _callbackHandler(map);
    }

    public SmsBean sendVerify2(String phoneNumber, String minute, String verify) {
        HashMap<String, Object> map = ccpRestSmsSDK.sendTemplateSMS(phoneNumber, registerTp2, new String[]{verify, minute});
        return _callbackHandler(map);
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

    /**
     * 根据推送类型获取对应的模版id
     */
    public String getSmsTemplateId(Integer operateType) {
        switch (operateType) {
            case 101:
                return orderCreateTpl;
            case 102:
                return orderDispatchTpl;
            case 103:
                return orderServiceDepartTpl;
            case 104:
                return orderSignupTpl;
            case 105:
                return assistOrderCreateTpl;
            case 106:
                return orderServiceGroupDepartTpl;
            case 107:
                return orderDispatchNotifyCustomer;
            case 108:
                return "";
            case 109:
                return changeServiceNotifyServiceTpl;
            case 110:
                return cycleSingleOrderCancelNotifyCustomer;
            case 111:
                return orderCancelNotifyCustomerTpl;
            case 112:
                return orderCancelNotifyCustomServiceTpl;
            case 113:
                return orderCancelNotifyServiceTpl;
            default:
                return "";
        }
    }
}
