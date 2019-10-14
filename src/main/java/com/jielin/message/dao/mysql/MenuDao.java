package com.jielin.message.dao.mysql;

import com.jielin.message.po.MenuPo;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@CacheConfig(cacheNames = "Menu")
public interface MenuDao {

    @Cacheable(key = "#root.methodName")
    List<MenuPo> selectAllParent();

    @Cacheable(key = "#root.methodName+':'+#parentId")
    List<MenuPo> selectAllChild(@Param("parentId") Integer parentId);

}