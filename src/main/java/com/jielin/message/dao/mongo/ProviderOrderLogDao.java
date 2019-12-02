package com.jielin.message.dao.mongo;

import com.jielin.message.po.ProviderOrderLog;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;


@Repository
public class ProviderOrderLogDao extends BaseDao<ProviderOrderLog> {


    public ProviderOrderLogDao(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }

    //查询悦姐的订单日志
    public ProviderOrderLog selectProviderOder(Integer providerId) {
        Criteria criteria = Criteria.where("providerId")
                .is(providerId);
        Query query = new Query(criteria);
        return super.mongoTemplate.findOne(query, getClz());
    }

}
