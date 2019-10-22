package com.jielin.message.dao.mysql;

import com.jielin.message.po.AuthMemberPo;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@CacheConfig(cacheNames = "authMember")
public interface AuthMemberDao {

    @Cacheable(key = "'selectByCustomId:'.concat(#customId.toString()).concat(#platform)")
    List<AuthMemberPo> selectByCustomId(@Param("customId") Integer customId, @Param("platform") String platform);

}