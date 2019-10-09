package com.jielin.message.dao.mysql;

import com.jielin.message.po.AdminRolePo;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = "adminRole")
public interface AdminRoleDao {

    //根据id查找用户权限
    @Cacheable(key = "'selectByPrimaryKey:'.concat(#id.toString())")
    AdminRolePo selectByPrimaryKey(@Param("id") Integer id);
}