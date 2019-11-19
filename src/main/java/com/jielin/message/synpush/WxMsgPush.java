package com.jielin.message.synpush;

import com.jielin.message.config.ThirdApiConfig;
import com.jielin.message.config.WeChatConfig;
import com.jielin.message.dao.mongo.OperateLogDao;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.dto.ResponsePackDto;
import com.jielin.message.dto.TemplateMsgResult;
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

import static com.jielin.message.util.MsgConstant.*;
import static com.jielin.message.util.enums.PushTypeEnum.WX_NP_PUSH;

/**
 * 微信公众号推送
 *
 * @author yxl
 */
@Component("wxMsgPush")
@Slf4j
public class WxMsgPush extends MsgPush {

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
                .addParameter("token",thirdApiConfig.getJlWebAccessToken())
                .addParameter("customId", paramDto.getUserId().toString())
                .addParameter("platform", platform)
                .build().toString();
        ResponseEntity<ResponsePackDto> authResult
                = restTemplate.exchange(authBuilder, ThirdActionEnum.JL_WEB_AUTH_MEMBER.getRequestType(), null, ResponsePackDto.class);
        String openid = null;
        if (authResult.getStatusCode().equals(HttpStatus.OK) &&
                authResult.getBody() != null) {
            ResponsePackDto body = authResult.getBody();
            if (body.getStatus() == 3){
                thirdApiConfig.init();
                this.pushMsg(paramDto);
            }
            HashMap map = (HashMap) authResult.getBody().getBody();
            openid = (String) map.get("openid");
        }
        if (StringUtils.isNotBlank(openid)) {
            //获取发送的模版数据
            String data = templateFactory.newTemplate(paramDto, WX_NP_PUSH.getType(), openid);
            if (StringUtils.isBlank(data)) {
                return false;
            }
            UriComponents builder = UriComponentsBuilder.fromHttpUrl(WeChatConfig.PUSH_TEMPLATE_MSG_URL)
                    .queryParam("access_token", wechatTokenHelper.getToken(true)).build();
            TemplateMsgResult templateMsgResult = restTemplate.exchange(builder.toUriString(),
                    HttpMethod.POST,
                    new HttpEntity<>(data, headers),
                    TemplateMsgResult.class).getBody();
            //当access_token无效时刷新缓存数据
            log.info("微信公众号推送结果：{}", templateMsgResult.toString());
            if (templateMsgResult.getErrcode() == 40001) {
                UriComponents retry = UriComponentsBuilder.fromHttpUrl(WeChatConfig.PUSH_TEMPLATE_MSG_URL)
                        .queryParam("access_token", wechatTokenHelper.getToken(false)).build();
                templateMsgResult = restTemplate.exchange(retry.toUriString(),
                        HttpMethod.POST,
                        new HttpEntity<>(data, headers),
                        TemplateMsgResult.class).getBody();
            } else if (templateMsgResult.getErrcode() != 0) {
                operateLogDao.insert(new OperateLog(templateMsgResult));
            }
            result = templateMsgResult.getErrcode() == 0;
        }
        return result;
    }
}
