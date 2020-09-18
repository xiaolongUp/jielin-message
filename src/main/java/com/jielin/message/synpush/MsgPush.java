package com.jielin.message.synpush;

import com.jielin.message.dao.mongo.MessageSendLogDao;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.po.MessageSendLog;
import com.jielin.message.po.OperatePo;
import com.jielin.message.util.enums.PushTypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

import java.util.ArrayList;
import java.util.List;

/**
 * 统一的消息处理抽象类
 *
 * @author yxl
 */
@Slf4j
@Getter
@Setter
public abstract class MsgPush {

    @Autowired
    private MessageSendLogDao messageSendLogDao;

    //为不同的线程当前推送参数,threadLocal存放参数的时候注意顺序，recover拿取强转
    private ThreadLocal<List> localParamDto = new ThreadLocal<>();

    //报错时，重试三次，每次间隔500毫秒
    @Retryable(value = Exception.class, backoff = @Backoff(delay = 500L, multiplier = 1))
    public abstract boolean pushMsg(ParamDto paramDto, OperatePo operatePo) throws Exception;

    //三次重试都失败后的处理方法
    @Recover
    public boolean recover(Exception e) {
        //报错时从当前线程拿出参数记录
        List list = localParamDto.get();
        ParamDto paramDto = (ParamDto) list.get(0);
        PushTypeEnum pushTypeEnum = (PushTypeEnum) list.get(2);
        log.error("correlationId:{},[" + pushTypeEnum.getDesc() + "]发送消息失败:{}", paramDto.getCorrelationId(), e.getMessage());
        log.error("消息推送失败：", e);
        insertMsgSendLog(paramDto, ((OperatePo) list.get(1)).getOperateName(), (PushTypeEnum) list.get(2), false, e.getMessage());
        localParamDto.remove();
        return false;
    }

    /**
     * 对应的handler支持的处理类型
     *
     * @param handlerType 对应类型为PushTypeEnum中的type，如需要处理新的推送模式，新增handler即可
     * @return if equals PushTypeEnum.type
     * @see PushTypeEnum
     */
    public abstract boolean supports(Integer handlerType);

    protected void insertMsgSendLog(ParamDto paramDto, String operateType, PushTypeEnum pushType, Boolean result, String msg) {
        MessageSendLog log = new MessageSendLog(paramDto, operateType, pushType.getType(),pushType.getDesc(), result, msg);
        if (pushType == PushTypeEnum.SYSTEM_PUSH) {
            log.setReadStatus(false);
        }
        messageSendLogDao.insert(log);
    }

    /**
     * threadLocal参数赋值
     *
     * @param paramDto     消息队列接收的参数
     * @param operatePo    处理类型对象
     * @param pushTypeEnum 处理类型枚举
     */
    protected void setThreadLocalParams(ParamDto paramDto, OperatePo operatePo, PushTypeEnum pushTypeEnum) {
        List<Object> list = new ArrayList<>();
        list.add(paramDto);
        list.add(operatePo);
        list.add(pushTypeEnum);
        this.localParamDto.set(list);
    }

}
