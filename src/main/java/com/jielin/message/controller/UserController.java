package com.jielin.message.controller;

import com.jielin.message.dto.ResponseDto;
import com.jielin.message.service.ProducerService;
import com.jielin.message.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户注册和发送验证码的方法
 *
 * @author yxl
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {


    @Autowired
    private UserService userService;


    @Autowired
    private ProducerService producerService;

    //注册成功发送短信验
    @GetMapping(value = "/register")
    public ResponseDto<Boolean> register(HttpServletRequest request,
                                         @RequestParam("phoneNumber") String phoneNumber) {
            //return userService.register(phoneNumber);
        producerService.send();
        return new ResponseDto<>();
    }

    //发送短信验证码
    @GetMapping(value = "/smscode")
    public ResponseDto<Boolean> smscode(HttpServletRequest request,
                                        @RequestParam("phoneNumber") String phoneNumber,
                                        @RequestParam("param") String[] param) {
            return userService.smscode(phoneNumber, param);
    }

}
