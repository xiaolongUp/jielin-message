package com.jielin.message.controller;

import com.jielin.message.dto.ParamDto;
import com.jielin.message.dto.ResponseDto;
import com.jielin.message.service.ProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/product")
public class ProducerController {

    @Autowired
    private ProducerService producerService;

    //生产消息，所有的系统都需要通过http请求调用该接口推送数据
    @PostMapping(value = "/msg")
    public ResponseDto productMsg(@RequestBody @Valid ParamDto paramDto) {
        producerService.send(paramDto);
        return new ResponseDto();
    }
}
