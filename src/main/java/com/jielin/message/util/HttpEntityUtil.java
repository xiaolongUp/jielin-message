package com.jielin.message.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class HttpEntityUtil {

    public static HttpEntity getHttpEntity(Object o) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        return new HttpEntity<>(o, headers);
    }
}
