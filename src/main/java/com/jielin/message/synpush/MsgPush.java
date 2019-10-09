package com.jielin.message.synpush;

import com.jielin.message.dao.mongo.OperateLogDao;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.po.OperateLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

/**
 * 统一的消息处理抽象类
 *
 * @author yxl
 */
@Slf4j
public abstract class MsgPush {

    @Autowired
    protected OperateLogDao operateLogDao;

    //报错时，重试三次，每次间隔500毫秒
    @Retryable(value = Exception.class, backoff = @Backoff(delay = 500L, multiplier = 1))
    public abstract boolean pushMsg(ParamDto paramDto) throws Exception;

    //三次重试都失败后的处理方法
    @Recover
    public boolean recover(Exception e) {
        log.error("MsgPush发送消息失败：{}", e.getMessage());
        operateLogDao.insert(new OperateLog(e.getMessage()));
        return false;
    }

}
