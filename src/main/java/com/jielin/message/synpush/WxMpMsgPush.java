package com.jielin.message.synpush;

import com.jielin.message.config.WeChatConfig;
import com.jielin.message.dao.mongo.OperateLogDao;
import com.jielin.message.dao.mysql.AuthMemberDao;
import com.jielin.message.dao.mysql.CustomUserDao;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.dto.TemplateMsgResult;
import com.jielin.message.po.AuthMemberPo;
import com.jielin.message.po.OperateLog;
import com.jielin.message.util.TemplateFactory;
import com.jielin.message.util.wechat.WechatTokenHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.util.List;

import static com.jielin.message.util.MsgConstant.PLATFORM_WECHAT_MP;
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
    private AuthMemberDao authMemberDao;

    @Autowired
    private CustomUserDao customUserDao;

    @Autowired
    protected OperateLogDao operateLogDao;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    WechatTokenHelper wechatTokenHelper;

    @Autowired
    private TemplateFactory templateFactory;

    private static HttpHeaders headers = new HttpHeaders();

    @PostConstruct
    public void init() {
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    }

    @Override
    public boolean pushMsg(ParamDto paramDto) throws Exception {
        boolean result = false;
        Integer customId = customUserDao.selectUserIdByPhone(paramDto.getPhoneNumber());
        List<AuthMemberPo> authMembers = authMemberDao.selectByCustomId(customId, PLATFORM_WECHAT_MP);
        if (!authMembers.isEmpty()) {
            //获取发送的模版数据
            String data = templateFactory.newTemplate(paramDto, WX_MP_PUSH.getType(), authMembers.get(0).getOpenid());
            if (StringUtils.isBlank(data)) {
                return false;
            }
            UriComponents builder = UriComponentsBuilder.fromHttpUrl(WeChatConfig.MP_PUSH_TEMPLATE_MSG_URL)
                    .queryParam("access_token", wechatTokenHelper.getMpToken(true)).build();
            TemplateMsgResult templateMsgResult = restTemplate.exchange(builder.toUriString(),
                    HttpMethod.POST,
                    new HttpEntity<>(data, headers),
                    TemplateMsgResult.class).getBody();
            //当access_token无效时刷新缓存数据
            log.info("微信小程序推送结果：{}", templateMsgResult.toString());
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
            result = templateMsgResult.getErrcode() == 0;
        }
        return result;
    }
}
