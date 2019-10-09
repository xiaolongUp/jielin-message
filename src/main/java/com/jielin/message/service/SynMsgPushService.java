package com.jielin.message.service;

import com.jielin.message.dao.mongo.OperateLogDao;
import com.jielin.message.dao.mysql.MsgPushDao;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.po.MsgPushPo;
import com.jielin.message.synpush.MsgPush;
import com.jielin.message.synpush.SmsMsgPush;
import com.jielin.message.synpush.WxMsgPush;
import com.jielin.message.util.enums.PushTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 同步推送service实现
 *
 * @author yxl
 */
@Slf4j
@Service
@Transactional
public class SynMsgPushService {

    @Autowired
    private Map<String, MsgPush> msgPushMap;

    @Autowired
    private OperateLogDao operateLogDao;

    @Autowired
    private MsgPushDao msgPushDao;

    @Autowired
    private SmsMsgPush smsMsgPush;

    //根据参数选择不同的推送方式
    public boolean push(ParamDto paramDto) {
        //当微信公众号没有关注且没有配置短信推送时，默认需要推送一条短信
        boolean defaultSmsPush = true;
        //获取需要推送消息的
        List<MsgPushPo> msgPushes = msgPushDao.selectByCondition(paramDto.getOperateType());
        for (MsgPushPo msgPushPo : msgPushes) {
            String pushHandler = PushTypeEnum.getMsgPush(msgPushPo);
            if (StringUtils.isNoneBlank(pushHandler)) {
                MsgPush push = msgPushMap.get(pushHandler);
                if (push instanceof SmsMsgPush || push instanceof WxMsgPush){
                    defaultSmsPush = false;
                }
                try {
                    push.pushMsg(paramDto);
                } catch (Exception e) {
                    //do nothing
                    //需要添加功能，在@Recover处修改
                }
            }
        }
        if (defaultSmsPush){
            try {
                smsMsgPush.pushMsg(paramDto);
            } catch (Exception e) {
                //do nothing
                //需要添加功能，在@Recover处修改
            }
        }
        return false;
    }

}
