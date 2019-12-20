package com.jielin.message.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "dingtalk")
@Data
@Slf4j
public class DingtalkConfig {

    private Long agentId;

    private String appKey;

    private String appSecret;

    private String contractProcessCode;

    private String accessToken;

    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    public void initToken() {
        UriComponents builder = UriComponentsBuilder.fromHttpUrl(DING_ACCESS_TOKEN_URL)
                .queryParam("appkey", this.appKey)
                .queryParam("appsecret", this.appSecret).build();

        Map<String, Object> result = restTemplate.exchange(builder.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {
                }).getBody();
        assert result != null;
        log.info(result.toString());
        assert (Integer) result.get("errcode") == 0;
        this.accessToken = (String) result.get("access_token");
    }

    public static final String DING_ACCESS_TOKEN_URL = "https://oapi.dingtalk.com/gettoken";

    public static final String DING_PUSH_MSG_URL = "https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2";


}
