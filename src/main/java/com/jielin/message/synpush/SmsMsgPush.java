package com.jielin.message.synpush;

import com.jielin.message.config.YunTXSmsConfig;
import com.jielin.message.dao.mongo.TemplateDao;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.dto.SmsBean;
import com.jielin.message.po.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class SmsMsgPush extends MsgPush {

    @Autowired
    private YunTXSmsConfig yunTXSmsConfig;

    @Autowired
    private TemplateDao templateDao;

    @Override
    public boolean pushMsg(ParamDto paramDto) throws Exception {
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
        for (String key : paramKeys) {
            params.add((String) paramDto.getParams().get(key));
        }
        SmsBean smsBean = yunTXSmsConfig.sendCommonSMS(paramDto.getPhoneNumber(), smsTemplateId,
                params.toArray(new String[params.size()]));
        return smsBean.getIsSuccess();
    }
}
