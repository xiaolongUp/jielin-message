package com.jielin.message.controller;

import com.jielin.message.dto.ParamDto;
import com.jielin.message.dto.ResponseDto;
import com.jielin.message.service.SynMsgPushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 同步推送调用的接口
 *
 * @author yxl
 */
@Controller
@RequestMapping("/push")
@Slf4j
public class SynMsgPushController {

    @Autowired
    private SynMsgPushService synMsgPushService;

    @PostMapping(value = "/syn", produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ResponseDto<Boolean> synPush(HttpServletRequest request,
                                        @RequestBody ParamDto paramDto) {
            synMsgPushService.push(paramDto);
            return new ResponseDto<>("test");
    }
}
