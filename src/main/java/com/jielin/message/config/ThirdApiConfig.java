package com.jielin.message.config;

import com.jielin.message.dto.ResponsePackDto;
import com.jielin.message.third.enums.ThirdActionEnum;
import com.jielin.message.util.HttpEntityUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "third")
@Getter
@Setter
public class ThirdApiConfig {

    private String jlWebApiUrl;

    private String jlWebUserName;

    private String jlWebPassword;

    private String jlWebAccessToken;

    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        String url = this.jlWebApiUrl + ThirdActionEnum.JL_WEB_ACCESS_TOKEN.getActionName();

        Map<String, String> params = new HashMap<>();
        params.put("username", this.jlWebUserName);
        params.put("password", this.jlWebPassword);
        ResponseEntity<ResponsePackDto> result
                = restTemplate.exchange(url, ThirdActionEnum.JL_WEB_ACCESS_TOKEN.getRequestType(), HttpEntityUtil.getHttpEntity(params), ResponsePackDto.class);
        if (result.getStatusCode().equals(HttpStatus.OK) &&
                null != result.getBody()) {
            HashMap map = (HashMap) result.getBody().getBody();
            this.jlWebAccessToken = (String) map.get("accessToken");
        }
    }
}
