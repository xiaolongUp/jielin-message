package com.jielin.message.synpush.mail;

import com.jielin.message.dao.mongo.TemplateDao;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.po.OperatePo;
import com.jielin.message.po.Template;
import com.jielin.message.synpush.MsgPush;
import com.jielin.message.util.TemplateFactory;
import com.jielin.message.util.constant.MailConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

import static com.jielin.message.util.enums.PushTypeEnum.Mail_PUSH;
import static com.jielin.message.util.enums.PushTypeEnum.SMS_PUSH;

/**
 * @author yxl
 * @description 邮件推送
 * @date 2021/7/7 4:21 下午
 * @since jdk1.8
 */
@Component("mailMsgPush")
@Slf4j
public class MailMsgPush extends MsgPush {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateDao templateDao;

    @Autowired
    private TemplateFactory templateFactory;

    @Override
    public boolean pushMsg(ParamDto paramDto, OperatePo operatePo) throws Exception {
        super.setThreadLocalParams(paramDto, operatePo, SMS_PUSH);
        SimpleMailMessage message = new SimpleMailMessage();
        String fromTo = (String) paramDto.getParams().get(MailConstant.FROM_TO);
        if (StringUtils.isNotBlank(fromTo)) {
            message.setFrom(fromTo);
        } else {
            message.setFrom(MailConstant.SEND_FROM);
        }
        List<String> mailTos = (List<String>) paramDto.getParams().get(MailConstant.SEND_TO);
        String subject = (String) paramDto.getParams().get(MailConstant.SUBJECT);
        if (CollectionUtils.isEmpty(mailTos) || StringUtils.isBlank(subject)) {
            insertMsgSendLog(paramDto, operatePo.getOperateName(), Mail_PUSH, false, "邮件推送收件人为空或者主题为空！");
            return false;
        }
        String content = (String) paramDto.getParams().get(MailConstant.CONTENT);
        String mailContent = null;
        if (StringUtils.isNotBlank(content)) {
            mailContent = content;
        } else {
            Template tmp = templateDao.
                    selectByOperateAndPushType(paramDto.getOperateType(), Mail_PUSH.getType());
            if (Objects.isNull(tmp)) {
                insertMsgSendLog(paramDto, operatePo.getOperateName(), Mail_PUSH, false, "邮件推送模版未配置！");
                return false;
            }
            mailContent = templateFactory.newTemplate(paramDto, Mail_PUSH.getType(), null);
            if (StringUtils.isBlank(mailContent)) {
                log.info("生成的空模版：{}", mailContent);
                return false;
            }

        }
        String[] receivers = new String[mailTos.size()];
        for (int i = 0; i < mailTos.size(); i++) {
            receivers[i] = mailTos.get(i);
        }
        message.setSubject(subject);
        message.setTo(receivers);
        message.setText(mailContent);
        mailSender.send(message);
        super.insertMsgSendLog(paramDto, operatePo.getOperateName(), Mail_PUSH, true, "发送邮件成功！");
        return true;
    }

    @Override
    public boolean supports(Integer handlerType) {
        return Mail_PUSH.getType() == handlerType;
    }
}
