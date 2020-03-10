package com.jielin.message.service;

import com.google.gson.Gson;
import com.jielin.message.dao.mongo.MsgPrepareSendLogDao;
import com.jielin.message.dao.mysql.OperateDao;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.po.MsgPrepareSendLog;
import com.jielin.message.po.MsgPushPo;
import com.jielin.message.po.OperatePo;
import com.jielin.message.po.OperatePoCriteria;
import com.jielin.message.synpush.MsgPush;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private Gson gson;

    @Autowired
    private Map<String, MsgPush> msgPushMap;

    @Autowired
    private OperateDao operateDao;

    @Autowired
    private SettingService settingService;

    private List<MsgPush> pushHandlers = new ArrayList<>();

    @Autowired
    private MsgPrepareSendLogDao msgPrepareSendLogDao;

    @PostConstruct
    public void init() {
        for (Map.Entry<String, MsgPush> msgPush : msgPushMap.entrySet()) {
            this.pushHandlers.add(msgPush.getValue());
        }
    }

    //根据参数选择不同的推送方式
    public boolean push(ParamDto paramDto) {
        boolean result = false;
        OperatePo operatePo;
        MsgPrepareSendLog prepareSendLog = new MsgPrepareSendLog();
        OperatePoCriteria criteria = new OperatePoCriteria();
        criteria.createCriteria()
                .andOperateTypeEqualTo(paramDto.getOperateType());
        List<OperatePo> operatePos = operateDao.selectByExample(criteria);
        if (!operatePos.isEmpty()) {
            operatePo = operatePos.get(0);
        } else {
            prepareSendLog.setCorrelationId(paramDto.getCorrelationId())
                    .setOperateType(paramDto.getOperateType())
                    .setMsg("未配置该方式的推送！");
            msgPrepareSendLogDao.insert(prepareSendLog);
            return false;
        }
        try {
            //获取需要推送消息的
            List<MsgPushPo> msgPushes = settingService.selectEnableByCondition(paramDto.getOperateType(),
                    PlatformService.platformMap.get(paramDto.getPlatform()), paramDto.getUserType());
            //将需要推送的数据存入数据库
            prepareSendLog.setCorrelationId(paramDto.getCorrelationId())
                    .setOperateType(paramDto.getOperateType())
                    .setPushTypeList(msgPushes.stream().map(MsgPushPo::getOptionValue).collect(Collectors.toList()))
                    .setParams(gson.toJson(paramDto));
            msgPrepareSendLogDao.insert(prepareSendLog);
            //所有的推送方式都去推送该消息
            if (operatePo.getPushAll()) {
                for (MsgPush pushHandler : pushHandlers) {
                    result = pushHandler.pushMsg(paramDto, operatePo);
                }
            } else {
                for (MsgPushPo msgPushPo : msgPushes) {
                    for (MsgPush pushHandler : pushHandlers) {
                        //判断当前的推送方式是否支持
                        boolean supports = pushHandler.supports(msgPushPo.getOptionValue());
                        if (supports) {
                            result = pushHandler.pushMsg(paramDto, operatePo);
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
