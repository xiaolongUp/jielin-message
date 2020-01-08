package com.jielin.message.controller;

import com.jielin.message.dao.mongo.TemplateDao;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.dto.ResponseDto;
import com.jielin.message.po.Template;
import com.jielin.message.service.SynMsgPushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    @Autowired
    private TemplateDao templateDao;

    @PostMapping(value = "/syn", produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ResponseDto<Boolean> synPush(@RequestBody ParamDto paramDto) {
        Boolean push = synMsgPushService.push(paramDto);
        return new ResponseDto<>(push);
    }


    @GetMapping(value = "/insert")
    @ResponseBody
    public void insert() {
        Template template = new Template();
        Map<String, String> params = new HashMap<>();
        params.put("keyword1", "customName");
        params.put("keyword2", "productName");
        params.put("keyword3", "money");
        params.put("keyword4", "serviceTime");
        template.setTmpId("103050")
                .setTmpName("订单创建成功短信模板")
                .setOperateType(101).setOptionType(1)
                .setTitle("尊敬的客户，您的服务购买成功")
                .setParamMap(params)
                .setRemark("请您在家等候，感谢配合。联络请致电400-0022-699")
                .setCreateTime(new Date())
                .setUpdateTime(new Date())
                .setEnable(true);
        templateDao.insert(template);
    }
}
