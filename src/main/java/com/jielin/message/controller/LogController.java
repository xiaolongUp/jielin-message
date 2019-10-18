package com.jielin.message.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 日志controller
 *
 * @author yxl
 */
@Controller
@RequestMapping("/log")
public class LogController {

    //获取错误日志
    @RequestMapping
    @ResponseBody
    public void getErrorLog(){

    }
}
