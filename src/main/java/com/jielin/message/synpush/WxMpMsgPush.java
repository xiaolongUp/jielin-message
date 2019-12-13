package com.jielin.message.synpush;

import com.jielin.message.config.ThirdApiConfig;
import com.jielin.message.config.WeChatConfig;
import com.jielin.message.dao.mongo.MessageSendLogDao;
import com.jielin.message.dao.mongo.OperateLogDao;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.dto.ResponsePackDto;
import com.jielin.message.dto.TemplateMsgResult;
import com.jielin.message.po.MessageSendLog;
import com.jielin.message.po.OperateLog;
import com.jielin.message.third.enums.ThirdActionEnum;
import com.jielin.message.util.TemplateFactory;
import com.jielin.message.util.enums.UserTypeEnum;
import com.jielin.message.util.wechat.WechatTokenHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;

import java.util.HashMap;

import static com.jielin.message.util.MsgConstant.PLATFORM_WECHAT_MP;
import static com.jielin.message.util.MsgConstant.YUEJIE_WECHAT_MP;
import static com.jielin.message.util.enums.PushTypeEnum.WX_MP_PUSH;

/**
 * 微信小程序推送
 *
 * @author yxl
 */
@Component("wxMpMsgPush")
@Slf4j
public class WxMpMsgPush extends MsgPush {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    protected OperateLogDao operateLogDao;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    WechatTokenHelper wechatTokenHelper;

    @Autowired
    private TemplateFactory templateFactory;

    @Autowired
    private ThirdApiConfig thirdApiConfig;

    @Autowired
    private MessageSendLogDao messageSendLogDao;

    private static HttpHeaders headers = new HttpHeaders();

    @PostConstruct
    public void init() {
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    }

    @Override
    public boolean pushMsg(ParamDto paramDto) throws Exception {
        boolean result = false;
        String platform;
        //悦姐小程序
        if (paramDto.getAppType().equals(UserTypeEnum.PROVIDER.getType())) {
            platform = YUEJIE_WECHAT_MP;
        }
        //用户小程序
        else {
            platform = PLATFORM_WECHAT_MP;
        }

        String authUrl = thirdApiConfig.getJlWebApiUrl() + ThirdActionEnum.JL_WEB_AUTH_MEMBER.getActionName();
        String authBuilder = new URIBuilder(authUrl)
                .addParameter("token", thirdApiConfig.getJlWebAccessToken())
                .addParameter("customId", paramDto.getUserId().toString())
                .addParameter("platform", platform)
                .build().toString();
        ResponseEntity<ResponsePackDto> authResult
                = restTemplate.exchange(authBuilder, ThirdActionEnum.JL_WEB_AUTH_MEMBER.getRequestType(), null, ResponsePackDto.class);
        String openid = null;
        if (null != authResult.getBody() && authResult.getBody().getStatus() == 3) {
            thirdApiConfig.init();
            this.pushMsg(paramDto);
        } else if (authResult.getStatusCode().equals(HttpStatus.OK) &&
                null != authResult.getBody()) {
            if (null != authResult.getBody().getBody()) {
                HashMap map = (HashMap) authResult.getBody().getBody();
                openid = (String) map.get("openid");
            }
        }
        if (StringUtils.isNotBlank(openid)) {
            //获取发送的模版数据
            String data = templateFactory.newTemplate(paramDto, WX_MP_PUSH.getType(), openid);
            if (StringUtils.isBlank(data)) {
                return false;
            }
            UriComponents builder = UriComponentsBuilder.fromHttpUrl(WeChatConfig.MP_PUSH_TEMPLATE_MSG_URL)
                    .queryParam("access_token", wechatTokenHelper.getMpToken(true)).build();
            TemplateMsgResult templateMsgResult = restTemplate.exchange(builder.toUriString(),
                    HttpMethod.POST,
                    new HttpEntity<>(data, headers),
                    TemplateMsgResult.class).getBody();
            if (templateMsgResult.getErrcode() == 40001) {
                UriComponents retry = UriComponentsBuilder.fromHttpUrl(WeChatConfig.MP_PUSH_TEMPLATE_MSG_URL)
                        .queryParam("access_token", wechatTokenHelper.getMpToken(false)).build();
                templateMsgResult = restTemplate.exchange(retry.toUriString(),
                        HttpMethod.POST,
                        new HttpEntity<>(data, headers),
                        TemplateMsgResult.class).getBody();
            } else if (templateMsgResult.getErrcode() != 0) {
                operateLogDao.insert(new OperateLog(templateMsgResult));
            }
            //当access_token无效时刷新缓存数据
            log.info("微信小程序推送结果：{}", templateMsgResult.toString());
            messageSendLogDao.insert(new MessageSendLog(paramDto, templateMsgResult.toString()));
            result = templateMsgResult.getErrcode() == 0;
        }
        return result;
    }
}
