package com.jielin.message.dao.mongo;

import com.mongodb.client.result.DeleteResult;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.lang.reflect.ParameterizedType;

/**
 * mongo查询的基础类
 *
 * @author yxl
 */
@Repository
@SuppressWarnings("unused")
public class BaseDao<T> {

    private final MongoTemplate mongoTemplate;

    /**
     * 创建一个 Class 的对象来获取泛型的 Class
     */
    private Class<T> clz;

    @Autowired
    public BaseDao(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    private Class<T> getClz() {
        if (clz == null) {
            clz = ((Class<T>) (((ParameterizedType) (this.getClass().getGenericSuperclass())).getActualTypeArguments()[0]));
        }
        return clz;
    }

    //删除数据
    public boolean remove(String id) {
        Criteria criteria = Criteria.where("_id").is(new ObjectId(id));
        Query query = new Query(criteria);
        DeleteResult deleteResult = mongoTemplate.remove(query, getClz());
        return deleteResult.getDeletedCount() > 0;
    }

    //查询数据
    public T get(String id) {
        Criteria criteria = Criteria.where("_id").is(id);
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, getClz());
    }

    //增加数据
    public void insert(T t) {
        mongoTemplate.insert(t);
    }

    //修改数据
    public void save(T t) {
        mongoTemplate.save(t);
    }
}
