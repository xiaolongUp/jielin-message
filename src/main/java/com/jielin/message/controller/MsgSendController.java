package com.jielin.message.controller;


import com.jielin.message.dao.mongo.MessageSendLogDao;
import com.jielin.message.po.MessageSendLog;
import com.jielin.message.util.version.annotation.ApiVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("msg")
public class MsgSendController {

    @Autowired
    private MessageSendLogDao messageSendLogDao;


    @ApiVersion()
    @GetMapping(value = "/{version}/result/{id}")
    public List<MessageSendLog> getResult(@PathVariable String msgId,
                                          @RequestParam("platform") Integer platform, @RequestParam("operateType") Integer operateType) {
        return messageSendLogDao.getByParams(msgId, platform, operateType);
    }

}
