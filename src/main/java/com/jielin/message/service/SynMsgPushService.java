package com.jielin.message.service;

import com.jielin.message.dao.mongo.OperateLogDao;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.po.MsgPushPo;
import com.jielin.message.synpush.MsgPush;
import com.jielin.message.synpush.SmsMsgPush;
import com.jielin.message.util.enums.PushTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.jielin.message.util.enums.PushTypeEnum.APP_PUSH;

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
    private SettingService settingService;

    @Autowired
    private SmsMsgPush smsMsgPush;

    //根据参数选择不同的推送方式
    public boolean push(ParamDto paramDto) {
        boolean result = false;
        //获取需要推送消息的
        List<MsgPushPo> msgPushes = settingService.selectEnableByCondition(paramDto.getOperateType());
        //当没有配置推送规则时，默认需要推送一条短信
        if (msgPushes.isEmpty()) {
            try {
                result = smsMsgPush.pushMsg(paramDto);
            } catch (Exception e) {
                //do nothing
            }
        }
        //当只配置app推送的时候，同样需要推送一条短信推送
        if (msgPushes.size() == 1
                && msgPushes.get(0).getOptionValue() == APP_PUSH.getType()) {
            try {
                result = smsMsgPush.pushMsg(paramDto);
            } catch (Exception e) {
                //do nothing
            }

        }
        for (MsgPushPo msgPushPo : msgPushes) {
            String pushHandler = PushTypeEnum.getMsgPush(msgPushPo);
            if (StringUtils.isNoneBlank(pushHandler)) {
                MsgPush push = msgPushMap.get(pushHandler);
                try {
                    result = push.pushMsg(paramDto);
                } catch (Exception e) {
                    //do nothing
                }
                //当有规则发送消息成功且发送消息的不为app时，中断
                if (result && !(push instanceof SmsMsgPush)) {
                    break;
                }
            }

        }
        return result;
    }

}
