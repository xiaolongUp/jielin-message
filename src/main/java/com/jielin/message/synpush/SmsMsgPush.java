package com.jielin.message.synpush;

import com.jielin.message.config.YunTXSmsConfig;
import com.jielin.message.dao.mongo.MessageSendLogDao;
import com.jielin.message.dao.mongo.TemplateDao;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.dto.SmsBean;
import com.jielin.message.po.MessageSendLog;
import com.jielin.message.po.Template;
import com.jielin.message.util.MsgConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.jielin.message.util.enums.PushTypeEnum.SMS_PUSH;

/**
 * 短信推送实现
 *
 * @author yxl
 */
@Component("smsMsgPush")
@Slf4j
public class SmsMsgPush extends MsgPush implements ApplicationContextAware {

    private ApplicationContext context;

    @Autowired
    private YunTXSmsConfig yunTXSmsConfig;

    @Autowired
    private TemplateDao templateDao;

    @Autowired
    private MessageSendLogDao messageSendLogDao;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public boolean pushMsg(ParamDto paramDto) throws Exception {

        String activeProfile = context.getEnvironment().getActiveProfiles()[0];

        if (MsgConstant.PROD_PROFILE.equalsIgnoreCase(activeProfile)) {
            //当没有该操作类型的短信模版时，直接返回
            Template template = templateDao.selectByOperateAndPushType(paramDto.getOperateType(), SMS_PUSH.getType());
            if (!Optional.ofNullable(template).isPresent()) {
                return false;
            }
            String smsTemplateId = template.getTmpId();
            if (StringUtils.isBlank(smsTemplateId)) {
                return false;
            }

            List<String> paramKeys = template.getParamKeys();
            List<String> params = new ArrayList<>();
            if (paramKeys.isEmpty()) {
                return false;
            }
            for (String key : paramKeys) {
                params.add((String) paramDto.getParams().get(key));
            }
            SmsBean smsBean = yunTXSmsConfig.sendCommonSMS(paramDto.getPhoneNumber(), smsTemplateId,
                    params.toArray(new String[params.size()]));
            messageSendLogDao.insert(new MessageSendLog(paramDto, smsBean.getMessage()));
            return smsBean.getIsSuccess();
        } else {
            log.info("测试环境和本地环境的短信发送：{}", paramDto.toString());
            return true;
        }
    }
}
