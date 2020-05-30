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
    protected ThreadLocal<List> localParamDto = new ThreadLocal<>();

    //报错时，重试三次，每次间隔500毫秒
    @Retryable(value = Exception.class, backoff = @Backoff(delay = 500L, multiplier = 1))
    public abstract boolean pushMsg(ParamDto paramDto, OperatePo operatePo) throws Exception;

    //三次重试都失败后的处理方法
    @Recover
    public boolean recover(Exception e) {
        //报错时从当前线程拿出参数记录
        List list = localParamDto.get();
        ParamDto paramDto = (ParamDto) list.get(0);
        log.error("correlationId:{},发送消息失败:{}", paramDto.getCorrelationId(), e.getMessage());
        insertMsgSendLog(paramDto, ((OperatePo) list.get(1)).getOperateName(), (PushTypeEnum) list.get(2), false, e.getMessage());
        localParamDto.remove();
        return false;
    }

    public abstract boolean supports(Integer handlerType);

    protected void insertMsgSendLog(ParamDto paramDto, String operateType, PushTypeEnum pushType, Boolean result, String msg) {
        messageSendLogDao.insert(new MessageSendLog(paramDto, operateType, pushType.getDesc(), result, msg));
    }
}
