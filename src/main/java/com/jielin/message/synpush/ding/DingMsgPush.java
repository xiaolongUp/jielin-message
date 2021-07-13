package com.jielin.message.synpush.ding;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.request.OapiUserGetByMobileRequest;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.dingtalk.api.response.OapiUserGetByMobileResponse;
import com.google.gson.Gson;
import com.jielin.message.config.DingtalkConfig;
import com.jielin.message.dao.mongo.TemplateDao;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.po.OperatePo;
import com.jielin.message.po.Template;
import com.jielin.message.synpush.MsgPush;
import com.jielin.message.util.TemplateFactory;
import com.jielin.message.util.constant.DingMsgConstant;
import com.jielin.message.util.constant.MsgConstant;
import com.jielin.message.util.enums.DingMsgTypeEnum;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.jielin.message.util.enums.PushTypeEnum.DING_PUSH;

@Component("dingMsgPush")
@Slf4j
public class DingMsgPush extends MsgPush {

    @Autowired
    private DingtalkConfig config;

    @Autowired
    private Gson gson;

    @Autowired
    private TemplateDao templateDao;

    @Autowired
    private TemplateFactory templateFactory;

    private DingTalkClient client = new DefaultDingTalkClient(DingtalkConfig.DING_PUSH_MSG_URL);

    private DingTalkClient userClient = new DefaultDingTalkClient(DingtalkConfig.GET_USER_BY_MOBILE_URL);

    @Override
    public boolean pushMsg(ParamDto paramDto, OperatePo operatePo) throws Exception {
        super.setThreadLocalParams(paramDto, operatePo, DING_PUSH);
        return push(paramDto, operatePo);
    }

    @Override
    public boolean supports(Integer handlerType) {
        return DING_PUSH.getType() == handlerType;
    }

    //钉钉推送
    public boolean push(ParamDto paramDto, OperatePo operatePo) throws ApiException {
        return push(paramDto, operatePo, true);
    }

    //钉钉推送，是否重试
    public boolean push(ParamDto paramDto, OperatePo operatePo, Boolean retry) throws ApiException {

        //获取用户的userId
        String dingUserId = getDingUserId(paramDto.getPhoneNumber(), true);

        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
        if (!Optional.ofNullable(dingUserId).isPresent()) {
            super.insertMsgSendLog(paramDto, operatePo.getOperateName(), DING_PUSH, false, "获取用户钉钉id失败!");
            return false;
        }
        request.setUseridList(dingUserId);
        request.setAgentId(config.getAgentId());
        request.setToAllUser(false);

        String content = (String) paramDto.getParams().get(MsgConstant.CONTENT);
        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        //当需要直接推送时，不走模版拼接
        if (StringUtils.isNotBlank(content)) {
            msg.setMsgtype(DingMsgTypeEnum.TEXT.getType());
            msg.setText(new OapiMessageCorpconversationAsyncsendV2Request.Text());
            msg.getText().setContent(content);
            request.setMsg(msg);
        } else {
            Template template = templateDao.selectByOperateAndPushType(paramDto.getOperateType(), DING_PUSH.getType());
            if (template == null) {
                super.insertMsgSendLog(paramDto, operatePo.getOperateName(), DING_PUSH, false, "未配置该类型消息钉钉模版!");
                return false;
            }
            String newTemplate = templateFactory.newTemplate(paramDto, DING_PUSH.getType(), null);
            switch ((String) paramDto.getParams().get(DingMsgConstant.DING_MSG_TYPE)) {
                case "text":
                    msg.setMsgtype(DingMsgTypeEnum.TEXT.getType());
                    msg.setText(new OapiMessageCorpconversationAsyncsendV2Request.Text());
                    msg.getText().setContent(newTemplate);
                    request.setMsg(msg);
                    break;
                case "image":
                    msg.setMsgtype(DingMsgTypeEnum.IMAGE.getType());
                    msg.setImage(new OapiMessageCorpconversationAsyncsendV2Request.Image());
                    msg.getImage().setMediaId((String) paramDto.getParams().get(DingMsgConstant.DING_MEDIA_ID));
                    request.setMsg(msg);
                    break;
                case "link":
                    msg.setMsgtype(DingMsgTypeEnum.LINK.getType());
                    msg.setLink(new OapiMessageCorpconversationAsyncsendV2Request.Link());
                    msg.getLink().setTitle((String) paramDto.getParams().get(DingMsgConstant.DING_LINK_TITLE));
                    msg.getLink().setText((String) paramDto.getParams().get(DingMsgConstant.DING_LINK_TEXT));
                    msg.getLink().setMessageUrl((String) paramDto.getParams().get(DingMsgConstant.DING_LINK_MESSAGE_URL));
                    msg.getLink().setPicUrl((String) paramDto.getParams().get(DingMsgConstant.DING_LINK_PIC_URL));
                    request.setMsg(msg);
                    break;
                case "action_card":
                    msg.setMsgtype(DingMsgTypeEnum.CARD.getType());
                    OapiMessageCorpconversationAsyncsendV2Request.ActionCard card = new OapiMessageCorpconversationAsyncsendV2Request.ActionCard();
                    List<OapiMessageCorpconversationAsyncsendV2Request.BtnJsonList> btns = new ArrayList<>();
                    OapiMessageCorpconversationAsyncsendV2Request.BtnJsonList btn = new OapiMessageCorpconversationAsyncsendV2Request.BtnJsonList();
                    btns.add(btn);
                    btn.setActionUrl((String) paramDto.getParams().get(DingMsgConstant.DING_CARD_ACTION_URL));
                    String s = "**营业额：**%s<br>**异常数：**%s";

                    btn.setTitle((String) paramDto.getParams().get(DingMsgConstant.DING_CARD_BTN_TITLE));
                    card.setBtnJsonList(btns);
                    card.setBtnOrientation("1");
                    card.setMarkdown(newTemplate);
                    card.setTitle((String) paramDto.getParams().get(DingMsgConstant.DING_CARD_TITLE));
                    msg.setActionCard(card);
                    request.setMsg(msg);
                    break;
                default:
                    throw new RuntimeException("不支持该类型的钉钉推送");
            }

        }

        OapiMessageCorpconversationAsyncsendV2Response response = client.execute(request, config.getAccessToken());

        //当推送结果不成功时，获取token，再重试，只重试一次
        if (!response.isSuccess() && retry) {
            log.error(response.getErrmsg());
            config.initToken();
            push(paramDto, operatePo, false);
        }
        super.insertMsgSendLog(paramDto, operatePo.getOperateName(), DING_PUSH, response.isSuccess(), gson.toJson(response));
        log.info("correlationId:{},钉钉推送推送结果:{}", paramDto.getCorrelationId(), gson.toJson(response));
        return response.isSuccess();
    }


    private String getDingUserId(String mobile, Boolean retry) throws ApiException {

        OapiUserGetByMobileRequest request = new OapiUserGetByMobileRequest();
        request.setMobile(mobile);

        OapiUserGetByMobileResponse execute = userClient.execute(request, config.getAccessToken());
        if (!execute.isSuccess() && retry) {
            log.error(execute.getErrmsg());
            config.initToken();
            return getDingUserId(mobile, false);
        }
        return execute.getUserid();
    }
}
