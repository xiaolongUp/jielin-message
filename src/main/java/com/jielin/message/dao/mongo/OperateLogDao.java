package com.jielin.message.dao.mongo;

import com.jielin.message.po.OperateLog;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 操作日志的mongoDao
 *
 * @author yxl
 */
@Repository
public class OperateLogDao extends BaseDao<OperateLog> {

    public OperateLogDao(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }
}
