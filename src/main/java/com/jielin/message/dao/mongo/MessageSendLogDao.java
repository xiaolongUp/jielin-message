package com.jielin.message.dao.mongo;

import com.jielin.message.po.MessageSendLog;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    public List<MessageSendLog> getByParams(String msgId, Integer platform, Integer operateType) {
        Criteria criteria = Criteria.where("msgId").is(msgId)
                .and("platform").is(platform.toString());
        if (operateType != null) {
            criteria.and("operateId").is(operateType);
        }
        Query query = new Query(criteria);
        return mongoTemplate.find(query, getClz());
    }
}
