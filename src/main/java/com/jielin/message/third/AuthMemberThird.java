package com.jielin.message.third;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class AuthMemberThird {

    @Autowired
    private RestTemplate restTemplate;


}
