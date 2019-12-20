package com.jielin.message.service;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.jielin.message.config.DingtalkConfig;
import com.jielin.message.dao.mongo.MessageSendLogDao;
import com.jielin.message.dto.DingParamDto;
import com.jielin.message.po.MessageSendLog;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DingSynMsgPushService {

    @Autowired
    private DingtalkConfig config;

    @Autowired
    private MessageSendLogDao messageSendLogDao;

    //钉钉推送
    public boolean push(DingParamDto paramDto) throws ApiException {
        return push(paramDto, true);
    }

    //钉钉推送，是否重试
    public boolean push(DingParamDto paramDto, Boolean retry) throws ApiException {

        DingTalkClient client = new DefaultDingTalkClient(DingtalkConfig.DING_PUSH_MSG_URL);

        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
        request.setUseridList(paramDto.getUserId());
        request.setAgentId(config.getAgentId());
        request.setToAllUser(false);

        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setMsgtype("text");
        msg.setText(new OapiMessageCorpconversationAsyncsendV2Request.Text());
        msg.getText().setContent(paramDto.getDingMsgContent());
        request.setMsg(msg);
        OapiMessageCorpconversationAsyncsendV2Response response = client.execute(request, config.getAccessToken());

        //当推送结果不成功时，获取token，再重试
        if (!response.isSuccess() && retry) {
            config.initToken();
            push(paramDto);
        }
        MessageSendLog log = new MessageSendLog();
        log.setUserId(paramDto.getUserId())
                .setOperateType("钉钉推送")
                .setParams(paramDto.toString())
                .setResult(response.getBody());
        messageSendLogDao.insert(log);
        return response.isSuccess();
    }
}
