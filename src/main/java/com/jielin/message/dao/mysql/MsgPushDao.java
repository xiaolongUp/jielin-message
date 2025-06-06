package com.jielin.message.dao.mysql;

import com.jielin.message.po.MsgPushPo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 消息推送的配置项
 *
 * @author yxl
 */
@Repository
public interface MsgPushDao {

    List<MsgPushPo> selectEnableByCondition(@Param("operateType") Integer operateType,
                                            @Param("platform") Integer platform, @Param("userType") String userType);

    List<MsgPushPo> selectAll();

    List<MsgPushPo> selectAllByCondition(MsgPushPo msgPushPo);

    int deleteById(@Param("id") Integer id);

    int addRecord(MsgPushPo msgPushPo);

    int updateRecord(MsgPushPo msgPushPo);
}