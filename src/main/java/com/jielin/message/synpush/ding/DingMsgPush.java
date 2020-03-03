package com.jielin.message.synpush.ding;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.request.OapiUserGetByMobileRequest;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.dingtalk.api.response.OapiUserGetByMobileResponse;
import com.jielin.message.config.DingtalkConfig;
import com.jielin.message.dao.mongo.MessageSendLogDao;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.po.MessageSendLog;
import com.jielin.message.synpush.MsgPush;
import com.jielin.message.util.enums.PushTypeEnum;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("dingMsgPush")
@Slf4j
public class DingMsgPush extends MsgPush {

    @Autowired
    private DingtalkConfig config;

    @Autowired
    private MessageSendLogDao messageSendLogDao;

    @Override
    public boolean pushMsg(ParamDto paramDto) throws Exception {
        return push(paramDto);
    }

    @Override
    public boolean supports(Integer handlerType) {
        return PushTypeEnum.DING_PUSH.getType() == handlerType;
    }

    //钉钉推送
    public boolean push(ParamDto paramDto) throws ApiException {
        return push(paramDto, true);
    }

    //钉钉推送，是否重试
    public boolean push(ParamDto paramDto, Boolean retry) throws ApiException {
        String userId = null;
        //获取用户的userId
        try {
            userId = getUserId(paramDto.getPhoneNumber());
        } catch (ApiException e) {
            return false;
        }

        DingTalkClient client = new DefaultDingTalkClient(DingtalkConfig.DING_PUSH_MSG_URL);

        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
        if (!Optional.ofNullable(userId).isPresent()) {
            return false;
        }
        request.setUseridList(userId);
        request.setAgentId(config.getAgentId());
        request.setToAllUser(false);

        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        switch (paramDto.getDingMsg().getDingMsgType()) {
            case "text":
                msg.setMsgtype("text");
                msg.setText(new OapiMessageCorpconversationAsyncsendV2Request.Text());
                msg.getText().setContent(paramDto.getDingMsg().getDingMsgContent());
                request.setMsg(msg);
                break;
            default:
                throw new RuntimeException("不支持该类型的钉钉推送");
        }

        OapiMessageCorpconversationAsyncsendV2Response response = client.execute(request, config.getAccessToken());

        //当推送结果不成功时，获取token，再重试，只重试一次
        if (!response.isSuccess() && retry) {
            log.error(response.getErrmsg());
            config.initToken();
            push(paramDto, false);
        }
        MessageSendLog mongoLog = new MessageSendLog();
        mongoLog.setUserId(userId)
                .setOperateType("钉钉推送")
                .setParams(paramDto.toString())
                .setResult(response.getBody());
        messageSendLogDao.insert(mongoLog);
        return response.isSuccess();
    }


    private String getUserId(String mobile) throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient(DingtalkConfig.GET_USER_BY_MOBILE_URL);
        OapiUserGetByMobileRequest request = new OapiUserGetByMobileRequest();
        request.setMobile(mobile);

        OapiUserGetByMobileResponse execute = client.execute(request, config.getAccessToken());
        return execute.getUserid();
    }
}
