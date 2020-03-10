package com.jielin.message.dao.mongo;

import com.jielin.message.po.MsgPrepareSendLog;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MsgPrepareSendLogDao extends BaseDao<MsgPrepareSendLog> {

    public MsgPrepareSendLogDao(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }
}