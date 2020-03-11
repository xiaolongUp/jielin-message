package com.jielin.message.synpush.wx;

import com.jielin.message.config.ThirdApiConfig;
import com.jielin.message.config.WeChatConfig;
import com.jielin.message.dao.mysql.MsgUserDao;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.dto.ResponsePackDto;
import com.jielin.message.dto.TemplateMsgResult;
import com.jielin.message.po.MsgUserPo;
import com.jielin.message.po.MsgUserPoCriteria;
import com.jielin.message.po.OperatePo;
import com.jielin.message.synpush.MsgPush;
import com.jielin.message.util.TemplateFactory;
import com.jielin.message.util.enums.ThirdActionEnum;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.jielin.message.util.constant.MsgConstant.PLATFORM_WECHAT_OA;
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
    RedisTemplate redisTemplate;

    @Autowired
    WechatTokenHelper wechatTokenHelper;

    @Autowired
    private TemplateFactory templateFactory;

    @Autowired
    private ThirdApiConfig thirdApiConfig;

    @Autowired
    private MsgUserDao msgUserDao;

    private static HttpHeaders headers = new HttpHeaders();

    @PostConstruct
    public void init() {
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    }

    @Override
    public boolean pushMsg(ParamDto paramDto, OperatePo operatePo) throws Exception {
        List<Object> list = new ArrayList<>();
        list.add(paramDto);
        list.add(operatePo);
        list.add(WX_NP_PUSH);
        super.localParamDto.set(list);
        String userType = paramDto.getUserType();
        Integer userId = paramDto.getUserId();
        Integer platform = paramDto.getPlatform();
        boolean result;

        String authUrl = thirdApiConfig.getJlWebApiUrl() + ThirdActionEnum.JL_WEB_AUTH_MEMBER.getActionName();
        String authBuilder = new URIBuilder(authUrl)
                .addParameter("customId", paramDto.getUserId().toString())
                .addParameter("platform", PLATFORM_WECHAT_OA)
                .build().toString();

        String openid = null;
        ResponseEntity<ResponsePackDto> remoteCall;
        MsgUserPo msgUserPo =
                msgUserDao.selectByCondition(platform, userType, userId);
        remoteCall
                = restTemplate.exchange(authBuilder, ThirdActionEnum.JL_WEB_AUTH_MEMBER.getRequestType(), null, ResponsePackDto.class);

        if (null != remoteCall && null != remoteCall.getBody() && remoteCall.getBody().getStatus() == 3) {
            this.pushMsg(paramDto, operatePo);
        } else if (null != remoteCall && remoteCall.getStatusCode().equals(HttpStatus.OK) &&
                null != remoteCall.getBody()) {
            if (null != remoteCall.getBody().getBody()) {
                HashMap map = (HashMap) remoteCall.getBody().getBody();
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
        if (StringUtils.isBlank(openid)) {

            if (null != msgUserPo && StringUtils.isNotBlank(msgUserPo.getUniappAlias())) {
                openid = msgUserPo.getWxGzhOpenid();
            }
        }
        if (StringUtils.isBlank(openid)) {
            super.insertMsgSendLog(paramDto, operatePo.getOperateName(), WX_NP_PUSH, false, "微信公众号openid不存在！");
            return false;
        }
        //获取发送的模版数据
        String template = templateFactory.newTemplate(paramDto, WX_NP_PUSH.getType(), openid);
        if (StringUtils.isBlank(template)) {
            super.insertMsgSendLog(paramDto, operatePo.getOperateName(), WX_NP_PUSH, false, "微信公众号模版不存在！");
            return false;
        }
        UriComponents builder = UriComponentsBuilder.fromHttpUrl(WeChatConfig.PUSH_TEMPLATE_MSG_URL)
                .queryParam("access_token", wechatTokenHelper.getToken(true)).build();
        TemplateMsgResult templateMsgResult = restTemplate.exchange(builder.toUriString(),
                HttpMethod.POST,
                new HttpEntity<>(template, headers),
                TemplateMsgResult.class).getBody();
        if (templateMsgResult.getErrcode() == 40001) {
            UriComponents retry = UriComponentsBuilder.fromHttpUrl(WeChatConfig.PUSH_TEMPLATE_MSG_URL)
                    .queryParam("access_token", wechatTokenHelper.getToken(false)).build();
            templateMsgResult = restTemplate.exchange(retry.toUriString(),
                    HttpMethod.POST,
                    new HttpEntity<>(template, headers),
                    TemplateMsgResult.class).getBody();
        }
        //当access_token无效时刷新缓存数据
        log.info("微信公众号推送结果：{}", templateMsgResult.toString());
        result = templateMsgResult.getErrcode() == 0;
        super.insertMsgSendLog(paramDto, operatePo.getOperateName(), WX_NP_PUSH, result, templateMsgResult.toString());
        return result;
    }

    @Override
    public boolean supports(Integer handlerType) {
        return WX_NP_PUSH.getType() == handlerType;
    }
}
