package com.jielin.message.synpush;

import com.jielin.message.config.ThirdApiConfig;
import com.jielin.message.config.WeChatConfig;
import com.jielin.message.dao.mongo.MessageSendLogDao;
import com.jielin.message.dao.mongo.OperateLogDao;
import com.jielin.message.dao.mysql.MsgUserDao;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.dto.ResponsePackDto;
import com.jielin.message.dto.TemplateMsgResult;
import com.jielin.message.po.MessageSendLog;
import com.jielin.message.po.MsgUserPo;
import com.jielin.message.po.MsgUserPoCriteria;
import com.jielin.message.po.OperateLog;
import com.jielin.message.third.enums.ThirdActionEnum;
import com.jielin.message.util.TemplateFactory;
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

import static com.jielin.message.util.MsgConstant.PLATFORM_WECHAT_OA;
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

    @Autowired
    private MessageSendLogDao messageSendLogDao;

    @Autowired
    private MsgUserDao msgUserDao;

    private static HttpHeaders headers = new HttpHeaders();

    @PostConstruct
    public void init() {
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    }

    @Override
    public boolean pushMsg(ParamDto paramDto) throws Exception {
        String userType = paramDto.getUserType();
        Integer userId = paramDto.getUserId();
        Integer platform = paramDto.getPlatform();
        boolean hasException = false;
        boolean result = false;

        String authUrl = thirdApiConfig.getJlWebApiUrl() + ThirdActionEnum.JL_WEB_AUTH_MEMBER.getActionName();
        String authBuilder = new URIBuilder(authUrl)
                .addParameter("customId", paramDto.getUserId().toString())
                .addParameter("platform", PLATFORM_WECHAT_OA)
                .build().toString();

        String openid = null;
        ResponseEntity<ResponsePackDto> authResult = null;
        MsgUserPo msgUserPo =
                msgUserDao.selectByCondition(platform, userType, userId);
        try {
            authResult
                    = restTemplate.exchange(authBuilder, ThirdActionEnum.JL_WEB_AUTH_MEMBER.getRequestType(), null, ResponsePackDto.class);
        } catch (Exception e) {
            log.error("获取微信小程序openid接口异常:{}", e.getMessage());
            hasException = true;
            if (null != msgUserPo && StringUtils.isNotBlank(msgUserPo.getWxGzhOpenid())) {
                openid = msgUserPo.getWxGzhOpenid();
            }
        }

        if (null != authResult && null != authResult.getBody() && authResult.getBody().getStatus() == 3) {
            this.pushMsg(paramDto);
        } else if (null != authResult && authResult.getStatusCode().equals(HttpStatus.OK) &&
                null != authResult.getBody()) {
            if (null != authResult.getBody().getBody()) {
                HashMap map = (HashMap) authResult.getBody().getBody();
                openid = (String) map.get("openid");
                if (null == msgUserPo) {
                    MsgUserPo record = new MsgUserPo();
                    record.setPlatform(platform);
                    record.setUserType(userType);
                    record.setUserId(userId);
                    record.setUserPhone(paramDto.getPhoneNumber());
                    record.setWxGzhOpenid(openid);
                    msgUserDao.insert(record);
                } else if (StringUtils.isBlank(msgUserPo.getUniappAlias())) {
                    msgUserPo.setWxGzhOpenid(openid);
                    MsgUserPoCriteria example = new MsgUserPoCriteria();
                    example.createCriteria()
                            .andUserIdEqualTo(userId)
                            .andUserTypeEqualTo(userType)
                            .andPlatformEqualTo(platform);
                    msgUserDao.updateByExample(msgUserPo, example);
                }
            }
        }
        //当调用接口没有发生异常且接口没有返回数据时，重试使用本地的存储数据
        if (StringUtils.isBlank(openid) && !hasException) {

            if (null != msgUserPo && StringUtils.isNotBlank(msgUserPo.getUniappAlias())) {
                openid = msgUserPo.getWxGzhOpenid();
            }
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
            //当access_token无效时刷新缓存数据
            log.info("微信公众号推送结果：{}", templateMsgResult.toString());
            messageSendLogDao.insert(new MessageSendLog(paramDto, WX_NP_PUSH.getDesc(), templateMsgResult.toString()));
            result = templateMsgResult.getErrcode() == 0;
        }
        return result;
    }
}
