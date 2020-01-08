package com.jielin.message.synpush;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.jielin.message.config.DingtalkConfig;
import com.jielin.message.dao.mongo.MessageSendLogDao;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.po.MessageSendLog;
import com.taobao.api.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("dingMsgPush")
public class DingMsgPush extends MsgPush {

    @Autowired
    private DingtalkConfig config;

    @Autowired
    private MessageSendLogDao messageSendLogDao;

    @Override
    public boolean pushMsg(ParamDto paramDto) throws Exception {
        return push(paramDto);
    }

    //钉钉推送
    public boolean push(ParamDto paramDto) throws ApiException {
        return push(paramDto, true);
    }

    //钉钉推送，是否重试
    public boolean push(ParamDto paramDto, Boolean retry) throws ApiException {

        DingTalkClient client = new DefaultDingTalkClient(DingtalkConfig.DING_PUSH_MSG_URL);

        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
        if (!Optional.ofNullable(paramDto.getDingMsg().getDingUserId()).isPresent()) {
            return false;
        }
        request.setUseridList(paramDto.getDingMsg().getDingUserId());
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
            config.initToken();
            push(paramDto, false);
        }
        MessageSendLog log = new MessageSendLog();
        log.setUserId(paramDto.getDingMsg().getDingUserId())
                .setOperateType("钉钉推送")
                .setParams(paramDto.toString())
                .setResult(response.getBody());
        messageSendLogDao.insert(log);
        return response.isSuccess();
    }
}
