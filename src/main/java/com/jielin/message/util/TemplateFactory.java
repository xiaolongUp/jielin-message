package com.jielin.message.util;


import com.jielin.message.dao.mongo.TemplateDao;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.dto.WechatMpTemplateMsg;
import com.jielin.message.dto.WechatTemplateMsg;
import com.jielin.message.po.Template;
import com.jielin.message.util.enums.PushTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 创建模版的工厂方法
 *
 * @author yxl
 */
@Component
public class TemplateFactory {

    @Autowired
    private TemplateDao templateDao;

    private static final String FIRST = "first";

    private static final String REMARK = "remark";

    private static final String COLOR = "#0000FF";

    public String newTemplate(ParamDto paramDto, Integer optionType, String toUser) {

        Template template = templateDao.
                selectByOperateAndPushType(paramDto.getOperateType(), optionType);
        if (!Optional.ofNullable(template).isPresent()) {
            return null;
        }
        String data = null;

        switch (PushTypeEnum.valueOf(optionType)) {
            //微信公众号推送
            case WX_NP_PUSH:
                data = getWxTemplate(paramDto, template, toUser);
                break;
            //微信小程序推送
            case WX_MP_PUSH:
                data = getWxMpTemplate(paramDto, template, toUser);
                break;
            //app推送和钉钉推送
            case APP_PUSH:
            case DING_PUSH:
                data = getAppTemplate(paramDto, template);
                break;
        }
        return data;
    }

    /**
     * 获取app的模版
     *
     * @param paramDto 接口调用的参数
     * @param template 数据库存储的模版数据
     * @return 需要发送的消息
     */
    private String getAppTemplate(ParamDto paramDto, Template template) {

        List<String> paramKeys = SortUtil.sortByMapKey(template.getParamMap());
        List<String> params = new ArrayList<>();
        for (String key : paramKeys) {
            params.add((String) paramDto.getParams().get(key));
        }
        return String.format(template.getExample(), params.toArray(new String[params.size()]));
    }

    /**
     * 获取微信公众号的模版
     *
     * @param paramDto 接口调用的参数
     * @param template 数据库存储的模版数据
     * @param toUser   发送给的用户
     * @return 需要发送的消息
     */
    private String getWxTemplate(ParamDto paramDto, Template template, String toUser) {
        WechatTemplateMsg templateMsg = new WechatTemplateMsg();
        templateMsg.setTemplate_id(template.getTmpId())
                .setTouser(toUser);
        if (StringUtils.isNotBlank(template.getTitle())) {
            templateMsg.add(FIRST, template.getTitle(), COLOR);
        }
        if (StringUtils.isNotBlank(template.getJumpAddress())) {
            templateMsg.setUrl(template.getJumpAddress());
        }
        for (Map.Entry<String, String> entry : template.getParamMap().entrySet()) {
            String value = String.valueOf(paramDto.getParams().get(entry.getValue()));
            templateMsg.add(entry.getKey(), value, COLOR);
        }
        if (StringUtils.isNotBlank(template.getRemark())) {
            templateMsg.add(REMARK, template.getRemark(), COLOR);
        }
        return templateMsg.toJsonStr();
    }

    /**
     * 获取微信小程序的模版
     *
     * @param paramDto 接口调用的参数
     * @param template 数据库存储的模版数据
     * @param toUser   发送给的用户
     * @return 需要发送的消息
     */
    private String getWxMpTemplate(ParamDto paramDto, Template template, String toUser) {
        WechatMpTemplateMsg templateMsg = new WechatMpTemplateMsg();
        templateMsg.setTemplate_id(template.getTmpId())
                .setTouser(toUser);
        if (StringUtils.isNotBlank(template.getJumpAddress())) {
            templateMsg.setPage(template.getJumpAddress());
        }
        for (Map.Entry<String, String> entry : template.getParamMap().entrySet()) {
            String value = String.valueOf(paramDto.getParams().get(entry.getValue()));
            templateMsg.add(entry.getKey(), value);
        }
        return templateMsg.toJsonStr();
    }
}
