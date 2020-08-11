package com.jielin.message.controller;


import com.jielin.message.dao.mongo.MessageSendLogDao;
import com.jielin.message.dto.ResponseDto;
import com.jielin.message.po.MessageSendLog;
import com.jielin.message.util.version.annotation.ApiVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/msg")
public class MsgSendController {

    @Autowired
    private MessageSendLogDao messageSendLogDao;


    @ApiVersion()
    @GetMapping(value = "/{version}/result")
    public List<MessageSendLog> getResult(@RequestParam("msgId") String msgId,
                                          @RequestParam("platform") Integer platform,
                                          @RequestParam(value = "operateType", required = false) Integer operateType) {
        return messageSendLogDao.getByParams(msgId, platform, operateType);
    }

    @ApiVersion()
    @GetMapping(value = "/{version}/system-msg")
    public ResponseDto getSystemMsg(
            @RequestParam(value = "msgId", required = false) String msgId,
            @RequestParam("phone") String phone,
            @RequestParam("operateId") Integer operateId,
            @RequestParam("platform") Integer platform) {
        return ResponseDto.success(messageSendLogDao.getSystemPushResult(msgId, phone, operateId, platform));
    }

    @ApiVersion()
    @PostMapping(value = "/{version}/system-msg/update")
    public ResponseDto systemMsgUpdate(
            @RequestParam(value = "msgIds") List<String> msgIds,
            @RequestParam("operateId") Integer operateId,
            @RequestParam("platform") Integer platform) {
        if (msgIds.isEmpty()) {
            return ResponseDto.fail("更新数据不允许为空");
        }
        long l = messageSendLogDao.updateSystemMsgStatus(msgIds, operateId, platform);
        return ResponseDto.success(l);
    }


}
