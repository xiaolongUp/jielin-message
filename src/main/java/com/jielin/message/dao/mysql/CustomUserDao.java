package com.jielin.message.dao.mysql;

import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

/**
 * 查询客户信息
 */
@Repository
@CacheConfig(cacheNames = "customUser")
public interface CustomUserDao {

    @Cacheable(key = "#root.methodName+':'+#phone")
    Integer selectUserIdByPhone(@Param("phone") String phone);

}
