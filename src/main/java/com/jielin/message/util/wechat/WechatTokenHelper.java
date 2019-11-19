package com.jielin.message.util.wechat;


import com.jielin.message.config.WeChatConfig;
import com.jielin.message.util.MsgConstant;
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
        getToken(false);
        getMpToken(false);
    }

    //获取微信公众号接口访问token或者小程序的调用接口的token
    public String getToken(boolean fromCache) {
        String gzhAccessToken;
        if (!fromCache) {
            gzhAccessToken = getgzhAccessToken();
        } else {
            String wxgzhAccessToken = redisTemplate.opsForValue().get(MsgConstant.WX_ACCESS_TOKEN);
            //当微信公众号token不存在时，获取token并保存
            if (StringUtils.isBlank(wxgzhAccessToken)) {
                gzhAccessToken = getgzhAccessToken();
            } else {
                gzhAccessToken = wxgzhAccessToken;
            }
        }
        return gzhAccessToken;
    }

    private String getgzhAccessToken() {
        UriComponents builder = UriComponentsBuilder.fromHttpUrl(WeChatConfig.ACCESS_TOKEN_URL)
                .queryParam("grant_type", "client_credential")
                .queryParam("appid", weChatConfig.getGzhAppid())
                .queryParam("secret", weChatConfig.getGzhAppsecret()).build();
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
        weChatConfig.setGzhAccessToken(wxToken);
        redisTemplate.opsForValue().set(MsgConstant.WX_ACCESS_TOKEN, wxToken, wxExpiresIn - 300, TimeUnit.SECONDS);
        return wxToken;
    }


    //获取微信小程序的调用接口的token
    public String getMpToken(boolean fromCache) {
        String mpAccessToken;
        if (!fromCache) {
            mpAccessToken = getMpAccessToken();
        } else {
            String wxMpAccessToken = redisTemplate.opsForValue().get(MsgConstant.WX_MP_ACCESS_TOKEN);
            //当微信小程序token不存在时，获取token并保存
            if (StringUtils.isBlank(wxMpAccessToken)) {
                mpAccessToken = getMpAccessToken();
            } else {
                mpAccessToken = wxMpAccessToken;
            }
        }
        return mpAccessToken;
    }

    private String getMpAccessToken() {
        UriComponents builder = UriComponentsBuilder.fromHttpUrl(WeChatConfig.ACCESS_TOKEN_URL)
                .queryParam("grant_type", "client_credential")
                .queryParam("appid", weChatConfig.getYjMpAppid())
                .queryParam("secret", weChatConfig.getYjMpAppsecret()).build();
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
        weChatConfig.setMpAccessToken(wxToken);
        redisTemplate.opsForValue().set(MsgConstant.WX_MP_ACCESS_TOKEN, wxToken, wxExpiresIn - 300, TimeUnit.SECONDS);
        return wxToken;
    }


}
