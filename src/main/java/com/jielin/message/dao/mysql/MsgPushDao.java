package com.jielin.message.dao.mysql;

import com.jielin.message.po.MsgPushPo;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 消息推送的配置项
 *
 * @author yxl
 */
@Repository
@CacheConfig(cacheNames = "msgPush")
public interface MsgPushDao {

    @Cacheable(key = "#root.methodName+':'+#operateType")
    List<MsgPushPo> selectByCondition(@Param("operateType") Integer operateType);

    @Cacheable(key = "#root.methodName")
    List<MsgPushPo> selectAll();

}