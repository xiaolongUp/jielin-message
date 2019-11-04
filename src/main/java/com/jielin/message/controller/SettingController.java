package com.jielin.message.controller;

import com.jielin.message.dto.PageData;
import com.jielin.message.dto.ResponseDto;
import com.jielin.message.po.MsgPushPo;
import com.jielin.message.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 全局的设置配置
 *
 * @author yxl
 */
@Controller
@RequestMapping("/setting")
public class SettingController {

    @Autowired
    private SettingService settingService;

    /**
     * 所有的推送需要的配置选项
     *
     * @return List<MsgPushPo>
     */
    @RequestMapping("/push")
    public String getAllMsgPush() {
        return "push/list";
    }


    /**
     * 按照分页查询所有的配置信息
     */
    @GetMapping(value = "/msgPush")
    @ResponseBody
    public ResponseDto<PageData<MsgPushPo>> getMsgPush(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            MsgPushPo msgPushPo) {
        if (Optional.ofNullable(msgPushPo).isPresent()) {
            return settingService.selectPageByCondition(pageNo, pageSize, msgPushPo);
        } else {
            return settingService.selectPage(pageNo, pageSize);
        }

    }

    @DeleteMapping("/msgPush")
    @ResponseBody
    public ResponseDto deleteMsgPush(@RequestParam("id") Integer id) {

        return settingService.deleteById(id);
    }

    @PostMapping("/msgPush")
    @ResponseBody
    public ResponseDto addMsgPush(@RequestBody MsgPushPo msgPushPo) {
        //更新数据
        if (Optional.ofNullable(msgPushPo.getId()).isPresent()) {
           return settingService.updateRecord(msgPushPo);
        }
        //新增数据
        else {
            return settingService.addRecord(msgPushPo);
        }
    }

}
