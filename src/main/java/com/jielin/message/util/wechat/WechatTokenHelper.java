package com.jielin.message.util.wechat;


import com.jielin.message.config.WeChatConfig;
import com.jielin.message.util.constant.MsgConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 获取微信access_token工具类
 *
 * @author yxl
 */
@Component
@Slf4j
public class WechatTokenHelper {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private WeChatConfig weChatConfig;

    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    private void initToken() {
        //初始化公众号的token
        for (WeChatConfig.WeChatGzh weChatGzh : weChatConfig.getWeChatGzhs()) {
            getGzhAccessToken(weChatGzh.getSystemApply());
        }
        for (WeChatConfig.WeChatMp weChatMp : weChatConfig.getWeChatMps()) {
            getMpAccessToken(weChatMp.getSystemApply());
        }
    }

    //获取微信公众号接口访问token或者小程序的调用接口的token
    public String getToken(boolean fromCache, String userType) {
        String prefix = userType + ":";
        String gzhAccessToken;
        if (!fromCache) {
            gzhAccessToken = getGzhAccessToken(userType);
        } else {
            String wxGzhAccessToken = redisTemplate.opsForValue().get(prefix + MsgConstant.WX_GZH_ACCESS_TOKEN);
            //当微信公众号token不存在时，获取token并保存
            if (StringUtils.isBlank(wxGzhAccessToken)) {
                gzhAccessToken = getGzhAccessToken(userType);
            } else {
                gzhAccessToken = wxGzhAccessToken;
            }
        }
        return gzhAccessToken;
    }

    private String getGzhAccessToken(String userType) {
        String prefix = userType + ":";
        for (WeChatConfig.WeChatGzh weChatGzh : weChatConfig.getWeChatGzhs()) {
            if (weChatGzh.support(userType)) {
                UriComponents builder = UriComponentsBuilder.fromHttpUrl(WeChatConfig.ACCESS_TOKEN_URL)
                        .queryParam("grant_type", "client_credential")
                        .queryParam("appid", weChatGzh.getAppid())
                        .queryParam("secret", weChatGzh.getAppsecret()).build();
                Map<String, Object> result = restTemplate.exchange(builder.toUriString(),
                        HttpMethod.POST,
                        null,
                        new ParameterizedTypeReference<Map<String, Object>>() {
                        }).getBody();
                assert result != null;
                log.info(result.toString());
                assert result.get("errcode") == null;
                String wxToken = (String) result.get(MsgConstant.WX_TOKEN_KEY);
                Integer wxExpiresIn = (Integer) result.get(MsgConstant.WX_EXPIRES_IN);
                weChatGzh.setAccessToken(wxToken);
                redisTemplate.opsForValue().set(prefix + MsgConstant.WX_GZH_ACCESS_TOKEN, wxToken, wxExpiresIn - 300, TimeUnit.SECONDS);
                return wxToken;
            }
        }
        throw new RuntimeException("未配置推送的公众号");
    }


    //获取微信小程序的调用接口的token
    public String getMpToken(boolean fromCache, String userType) {
        String prefix = userType + ":";
        String mpAccessToken;
        if (!fromCache) {
            mpAccessToken = getMpAccessToken(userType);
        } else {
            String wxMpAccessToken = redisTemplate.opsForValue().get(prefix + MsgConstant.WX_MP_ACCESS_TOKEN);
            //当微信小程序token不存在时，获取token并保存
            if (StringUtils.isBlank(wxMpAccessToken)) {
                mpAccessToken = getMpAccessToken(userType);
            } else {
                mpAccessToken = wxMpAccessToken;
            }
        }
        return mpAccessToken;
    }

    private String getMpAccessToken(String userType) {
        String prefix = userType + ":";
        for (WeChatConfig.WeChatMp weChatMp : weChatConfig.getWeChatMps()) {
            if (weChatMp.support(userType)) {
                UriComponents builder = UriComponentsBuilder.fromHttpUrl(WeChatConfig.ACCESS_TOKEN_URL)
                        .queryParam("grant_type", "client_credential")
                        .queryParam("appid", weChatMp.getAppid())
                        .queryParam("secret", weChatMp.getAppsecret()).build();
                Map<String, Object> result = restTemplate.exchange(builder.toUriString(),
                        HttpMethod.POST,
                        null,
                        new ParameterizedTypeReference<Map<String, Object>>() {
                        }).getBody();
                assert result != null;
                log.info(result.toString());
                assert result.get("errcode") == null;
                String wxToken = (String) result.get(MsgConstant.WX_TOKEN_KEY);
                Integer wxExpiresIn = (Integer) result.get(MsgConstant.WX_EXPIRES_IN);
                weChatMp.setAccessToken(wxToken);
                redisTemplate.opsForValue().set(prefix + MsgConstant.WX_MP_ACCESS_TOKEN, wxToken, wxExpiresIn - 300, TimeUnit.SECONDS);
                return wxToken;
            }
        }
        throw new RuntimeException("未配置推送的小程序");
    }


}
