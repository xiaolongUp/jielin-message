package com.jielin.message.dao.mongo;

import com.jielin.message.po.MessageSendLog;
import com.jielin.message.po.OperateLog;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 操作日志的mongoDao
 *
 * @author yxl
 */
@Repository
public class MessageSendLogDao extends BaseDao<MessageSendLog> {

    public MessageSendLogDao(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }
}
