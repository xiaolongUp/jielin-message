package com.jielin.message.synpush.sms;

import com.jielin.message.config.YunTXSmsConfig;
import com.jielin.message.dao.mongo.TemplateDao;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.dto.SmsBean;
import com.jielin.message.po.OperatePo;
import com.jielin.message.po.Template;
import com.jielin.message.synpush.MsgPush;
import com.jielin.message.util.SortUtil;
import com.jielin.message.util.constant.MsgConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
    private JavaMailSender mailSender;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public boolean pushMsg(ParamDto paramDto, OperatePo operatePo) throws Exception {
        super.setThreadLocalParams(paramDto, operatePo, SMS_PUSH);
        String activeProfile = context.getEnvironment().getActiveProfiles()[0];

        if (MsgConstant.PROD_PROFILE.equalsIgnoreCase(activeProfile)) {
            //当没有该操作类型的短信模版时，直接返回
            Template template = templateDao.selectByOperateAndPushType(paramDto.getOperateType(), SMS_PUSH.getType());
            if (!Optional.ofNullable(template).isPresent()) {
                super.insertMsgSendLog(paramDto, operatePo.getOperateName(), SMS_PUSH, false, "短信模版不存在！");
                return false;
            }
            String smsTemplateId = template.getTmpId();
            if (StringUtils.isBlank(smsTemplateId)) {
                super.insertMsgSendLog(paramDto, operatePo.getOperateName(), SMS_PUSH, false, "短信模版id不存在！");
                return false;
            }

            List<String> paramKeys = SortUtil.sortByMapKey(template.getParamMap());
            List<String> params = new ArrayList<>();
            if (paramKeys.isEmpty()) {
                super.insertMsgSendLog(paramDto, operatePo.getOperateName(), SMS_PUSH, false, "短信发送的数据未配置！");
                return false;
            }
            for (String key : paramKeys) {
                params.add((String) paramDto.getParams().get(key));
            }
            SmsBean smsBean = yunTXSmsConfig.sendCommonSMS(paramDto.getPhoneNumber(), smsTemplateId,
                    params.toArray(new String[params.size()]));

            super.insertMsgSendLog(paramDto, operatePo.getOperateName(), SMS_PUSH, smsBean.getIsSuccess(), smsBean.getMessage());
            log.info("correlationId:{},短信推送结果:{}", paramDto.getCorrelationId(), smsBean.getMessage());
            return smsBean.getIsSuccess();
        } else {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("ping@yueguanjia.com");
            message.setTo("zhenyang.shen@yueguanjia.com");
            message.setSubject("推送服务测试环境模拟发送短信");
            message.setText("测试环境发送短信：参数【" + paramDto.toString() + "】");
            mailSender.send(message);
            super.insertMsgSendLog(paramDto, operatePo.getOperateName(), SMS_PUSH, true, "测试环境发送短信！");
            log.info("correlationId:{},测试环境和本地环境的短信发送：{}", paramDto.getCorrelationId(), paramDto.toString());
            return true;
        }
    }

    @Override
    public boolean supports(Integer handlerType) {
        return SMS_PUSH.getType() == handlerType;
    }
}
