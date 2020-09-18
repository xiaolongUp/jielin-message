package com.jielin.message.synpush.wx;

import com.jielin.message.config.WeChatConfig;
import com.jielin.message.dao.mysql.MsgThirdDao;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.dto.TemplateMsgResult;
import com.jielin.message.po.MsgThirdPo;
import com.jielin.message.po.MsgThirdPoCriteria;
import com.jielin.message.po.OperatePo;
import com.jielin.message.synpush.MsgPush;
import com.jielin.message.util.TemplateFactory;
import com.jielin.message.util.wechat.WechatTokenHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.net.URISyntaxException;
import java.util.List;

import static com.jielin.message.util.enums.PushTypeEnum.WX_GZH_PUSH;

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
    RedisTemplate redisTemplate;

    @Autowired
    WechatTokenHelper wechatTokenHelper;

    @Autowired
    private TemplateFactory templateFactory;

    @Autowired
    private MsgThirdDao thirdDao;

    private static HttpHeaders headers = new HttpHeaders();

    @PostConstruct
    public void init() {
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    }

    @Override
    public boolean pushMsg(ParamDto paramDto, OperatePo operatePo) throws Exception {
        super.setThreadLocalParams(paramDto, operatePo, WX_GZH_PUSH);
        boolean result;

        String openid = getOpenid(paramDto, operatePo);
        if (StringUtils.isBlank(openid)) {
            return false;
        }
        //获取发送的模版数据
        String template = templateFactory.newTemplate(paramDto, WX_GZH_PUSH.getType(), openid);
        if (StringUtils.isBlank(template)) {
            super.insertMsgSendLog(paramDto, operatePo.getOperateName(), WX_GZH_PUSH, false, "微信公众号模版不存在！");
            return false;
        }
        UriComponents builder = UriComponentsBuilder.fromHttpUrl(WeChatConfig.PUSH_TEMPLATE_MSG_URL)
                .queryParam("access_token", wechatTokenHelper.getToken(true, paramDto.getUserType())).build();
        TemplateMsgResult templateMsgResult = restTemplate.exchange(builder.toUriString(),
                HttpMethod.POST,
                new HttpEntity<>(template, headers),
                TemplateMsgResult.class).getBody();
        if (templateMsgResult.getErrcode() == 40001) {
            UriComponents retry = UriComponentsBuilder.fromHttpUrl(WeChatConfig.PUSH_TEMPLATE_MSG_URL)
                    .queryParam("access_token", wechatTokenHelper.getToken(false, paramDto.getUserType())).build();
            templateMsgResult = restTemplate.exchange(retry.toUriString(),
                    HttpMethod.POST,
                    new HttpEntity<>(template, headers),
                    TemplateMsgResult.class).getBody();
        } else if (templateMsgResult.getErrcode() == 40037 || templateMsgResult.getErrcode() == 43101) {
            super.insertMsgSendLog(paramDto, operatePo.getOperateName(), WX_GZH_PUSH, false, templateMsgResult.toString());
            return false;
        }
        //当access_token无效时刷新缓存数据
        log.info("微信公众号推送结果：{}", templateMsgResult.toString());
        result = templateMsgResult.getErrcode() == 0;
        super.insertMsgSendLog(paramDto, operatePo.getOperateName(), WX_GZH_PUSH, result, templateMsgResult.toString());
        return result;
    }

    /**
     * 通过第三方接口获取用户的小程序openid
     */
    private String getOpenid(ParamDto paramDto, OperatePo operatePo) throws URISyntaxException {
        String openid = null;
        //通过平台类型和推送类型获取调用的接口返回用户登录过的别名相关信息
        MsgThirdPoCriteria criteria = new MsgThirdPoCriteria();
        criteria.createCriteria()
                .andPlatformEqualTo(paramDto.getPlatform())
                .andUserTypeEqualTo(paramDto.getUserType())
                .andPushTypeEqualTo(WX_GZH_PUSH.getType());
        List<MsgThirdPo> msgThirdPos = thirdDao.selectByExample(criteria);
        if (CollectionUtils.isEmpty(msgThirdPos)) {
            super.insertMsgSendLog(paramDto, operatePo.getOperateName(), WX_GZH_PUSH, false, "未配置获取公众号第三方接口");
            return null;
        }
        //通过第三方接口查询对应的cid绑定信息
        MsgThirdPo msgThird = msgThirdPos.get(0);
        String authBuilder = new URIBuilder(msgThird.getUrl())
                .addParameter("phone", paramDto.getPhoneNumber())
                .build().toString();
        log.error("微信公众号获取openid远程调用地址:{}", authBuilder);
        ResponseEntity<String> remoteCall = restTemplate.exchange(authBuilder, HttpMethod.resolve(msgThird.getHttpMethod().toUpperCase()), null, String.class);

        if (remoteCall != null && remoteCall.getStatusCode().equals(HttpStatus.OK) &&
                null != remoteCall.getBody()) {
            openid = remoteCall.getBody();
        }
        //当调用接口没有发生异常且接口没有返回数据时
        if (StringUtils.isBlank(openid)) {
            super.insertMsgSendLog(paramDto, operatePo.getOperateName(), WX_GZH_PUSH, false, "微信公众号openid不存在！");
            return null;
        }
        return openid;
    }

    @Override
    public boolean supports(Integer handlerType) {
        return WX_GZH_PUSH.getType() == handlerType;
    }
}
