package com.jielin.message.dao.mongo;


import com.jielin.message.po.Template;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * 模版参数存在mongo当中
 *
 * @author yxl
 */
@Repository
@CacheConfig(cacheNames = "Template")
public class TemplateDao extends BaseDao<Template> {

    public TemplateDao(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }

    //查询所有符合的模版
    @Cacheable(key = "#root.methodName+':'+#operateType.toString()+':'+#optionType")
    public Template selectByOperateAndPushType(Integer operateType, Integer optionType) {
        Criteria criteria = Criteria.where("operateType")
                .is(operateType).and("optionType").is(optionType);
        Query query = new Query(criteria);
        return super.mongoTemplate.findOne(query, getClz());
    }
}
