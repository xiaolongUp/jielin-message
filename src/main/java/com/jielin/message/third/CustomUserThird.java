package com.jielin.message.third;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CustomUserThird {

    @Autowired
    private RestTemplate restTemplate;
}
