package com.jielin.message.dao.mysql;

import com.jielin.message.po.AdminUserPo;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = "adminUser")
public interface AdminUserDao {

    //查找用户
    @Cacheable(key = "'findUserByUsername:'.concat(#userName.toString())")
    AdminUserPo findUserByUsername(@Param("userName") String userName);

}