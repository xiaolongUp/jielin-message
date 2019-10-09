package com.jielin.message.synpush;

import com.jielin.message.config.YunTXSmsConfig;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.dto.SmsBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 短信推送实现
 *
 * @author yxl
 */
@Component("smsMsgPush")
@Slf4j
public class SmsMsgPush extends MsgPush {

    @Autowired
    private YunTXSmsConfig yunTXSmsConfig;

    @Override
    public boolean pushMsg(ParamDto paramDto) throws Exception {
        //当没有该操作类型的短信模版时，直接返回
        String smsTemplateId = yunTXSmsConfig.getSmsTemplateId(paramDto.getOperateType());
        if (StringUtils.isBlank(smsTemplateId)) {
            return false;
        }
        SmsBean smsBean = yunTXSmsConfig.sendCommonSMS(paramDto.getPhoneNumber(), yunTXSmsConfig.getSmsTemplateId(paramDto.getOperateType()),
                paramDto.getParams().toArray(new String[paramDto.getParams().size()]));
        return smsBean.getIsSuccess();
    }

    //注册短信通知
    public boolean register(String phoneNumber) {
        SmsBean smsBean = yunTXSmsConfig.sendCommonSMS(phoneNumber, yunTXSmsConfig.getCustomerRegisterNotify(), null);
        return smsBean.getIsSuccess();
    }

    //短信验证码
    public boolean smscode(String phoneNumber,String[] param) {
        SmsBean smsBean = yunTXSmsConfig.sendCommonSMS(phoneNumber, yunTXSmsConfig.getRegisterTpl(), param);
        return smsBean.getIsSuccess();
    }
}
