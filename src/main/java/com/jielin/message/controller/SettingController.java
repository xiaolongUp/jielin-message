package com.jielin.message.controller;

import com.jielin.message.dao.mysql.MsgPushDao;
import com.jielin.message.po.MsgPushPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 全局的设置配置
 *
 * @author yxl
 */
@Controller
@RequestMapping("/setting")
public class SettingController {

    @Autowired
    private MsgPushDao msgPushDao;

    /**
     * 所有的推送需要的配置选项
     *
     * @return List<MsgPushPo>
     */
    @RequestMapping("/push")
    public String getAllMsgPush() {
        List<MsgPushPo> msgPushPos = msgPushDao.selectAll();
        return "push/list";
    }
}
