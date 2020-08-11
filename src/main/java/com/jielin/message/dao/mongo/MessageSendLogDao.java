package com.jielin.message.dao.mongo;

import com.jielin.message.po.MessageSendLog;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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

    public List<MessageSendLog> getSystemPushResult(String msgId, String phone, Integer operateId, Integer platform) {

        Criteria criteria = Criteria.where("platform").is(platform.toString())
                .and("phone").is(phone).and("operateId").is(operateId);
        if (msgId != null) {
            criteria.and("msgId").is(msgId);
        }
        Query query = new Query(criteria);
        return mongoTemplate.find(query, getClz());
    }

    public long updateSystemMsgStatus(List<String> msgIds, Integer operateId, Integer platform) {
        Criteria criteria = Criteria.where("platform").is(platform.toString())
                .and("operateId").is(operateId)
                .and("msgId").in(msgIds);
        Query query = new Query(criteria);
        Update update = new Update().set("readStatus", true);
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, getClz());
        return updateResult.getModifiedCount();
    }
}
