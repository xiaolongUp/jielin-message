package com.jielin.message.dao.mysql;

import com.jielin.message.po.AppTemplatePo;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = "appTemplate")
public interface AppTemplateDao {


    @Cacheable(key = "'selectByType:'.concat(#type.toString())")
    AppTemplatePo selectByType(@Param("type") Integer type);

}