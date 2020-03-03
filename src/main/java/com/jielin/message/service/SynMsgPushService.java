package com.jielin.message.service;

import com.jielin.message.dao.mysql.OperateDao;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.po.MsgPushPo;
import com.jielin.message.po.OperatePo;
import com.jielin.message.po.OperatePoCriteria;
import com.jielin.message.synpush.MsgPush;
import com.jielin.message.synpush.sms.SmsMsgPush;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
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
    private OperateDao operateDao;

    @Autowired
    private SettingService settingService;

    @Autowired
    private SmsMsgPush smsMsgPush;

    private List<MsgPush> pushHandlers = new ArrayList<>();

    @PostConstruct
    public void init() {
        for (Map.Entry<String, MsgPush> msgPush : msgPushMap.entrySet()) {
            this.pushHandlers.add(msgPush.getValue());
        }
    }

    //根据参数选择不同的推送方式
    public boolean push(ParamDto paramDto) {
        boolean result = false;
        OperatePoCriteria criteria = new OperatePoCriteria();
        criteria.createCriteria()
                .andOperateTypeEqualTo(paramDto.getOperateType())
                .andPushAllEqualTo(true);
        List<OperatePo> operatePos = operateDao.selectByExample(criteria);
        try {
            //所有的推送方式都去推送该消息
            if (!operatePos.isEmpty()) {
                for (MsgPush pushHandler : pushHandlers) {
                    result = pushHandler.pushMsg(paramDto);
                }
            } else {
                //获取需要推送消息的
                List<MsgPushPo> msgPushes = settingService.selectEnableByCondition(paramDto.getOperateType(),
                        PlatformService.platformMap.get(paramDto.getPlatform()), paramDto.getUserType());
                //当没有配置推送规则时，默认需要推送一条短信
                if (msgPushes.isEmpty()) {
                    result = smsMsgPush.pushMsg(paramDto);
                }
                //当只配置app推送的时候，同样需要推送一条短信推送
                if (msgPushes.size() == 1
                        && msgPushes.get(0).getOptionValue() == APP_PUSH.getType()) {
                    log.info("推送了一条短信：" + paramDto.toString());
                    result = smsMsgPush.pushMsg(paramDto);

                }
                for (MsgPushPo msgPushPo : msgPushes) {
                    for (MsgPush pushHandler : pushHandlers) {
                        //判断当前的推送方式释放支持
                        boolean supports = pushHandler.supports(msgPushPo.getOptionValue());
                        if (supports) {
                            result = pushHandler.pushMsg(paramDto);
                        }
                    }
                }
            }

        } catch (Exception e) {
            //do nothing
        }

        return result;
    }

}
