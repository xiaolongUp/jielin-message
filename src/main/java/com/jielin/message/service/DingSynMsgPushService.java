package com.jielin.message.service;

import com.jielin.message.config.DingtalkConfig;
import com.jielin.message.dto.DingParamDto;
import com.jielin.message.dto.TextDingMsg;
import com.jielin.message.util.HttpEntityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class DingSynMsgPushService {

    @Autowired
    private DingtalkConfig config;

    @Autowired
    private RestTemplate restTemplate;

    //钉钉推送
    public boolean push(DingParamDto paramDto) {
        UriComponents builder = UriComponentsBuilder.fromHttpUrl(DingtalkConfig.DING_PUSH_MSG_URL)
                .queryParam("access_token", config.getAccessToken()).build();

        Map<String, Object> map = new HashMap<>();
        map.put("userid_list", paramDto.getUserId());
        map.put("agent_id", config.getAgentId());
        TextDingMsg msg = new TextDingMsg();
        msg.setText(new TextDingMsg.Text(paramDto.getDingMsgContent()));
        map.put("msg", msg);
        Map<String, Object> result = restTemplate.exchange(builder.toUriString(),
                HttpMethod.POST,
                HttpEntityUtil.getHttpEntity(map),
                new ParameterizedTypeReference<Map<String, Object>>() {
                }).getBody();
        if (result == null) {
            throw new RuntimeException("钉钉推送消息失败！");
        }
        log.info(result.toString());
        if (0 != (Integer) result.get("errcode")) {
            config.initToken();
            push(paramDto);
        }
        return 0 == (Integer) result.get("errcode");
    }
}
