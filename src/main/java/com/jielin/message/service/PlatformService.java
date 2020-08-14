package com.jielin.message.service;

import com.jielin.message.dao.mysql.MsgPlatformDao;
import com.jielin.message.po.MsgPlatform;
import com.jielin.message.po.MsgPlatformCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PlatformService {

    @Autowired
    private MsgPlatformDao msgPlatformDao;

    public static Map<Integer, Integer> platformMap = new HashMap<>();

    /**
     * 初始化所有的平台code与数据库存储id的关系
     */
    @PostConstruct
    public void init() {
        List<MsgPlatform> msgPlatforms =
                msgPlatformDao.selectByExample(new MsgPlatformCriteria());
        for (MsgPlatform msgPlatform : msgPlatforms) {
            if (msgPlatform.getPlatformCode() != null) {
                platformMap.put(msgPlatform.getPlatformCode(), msgPlatform.getId());
            }
        }
    }

    /**
     * 当数据库添加新的平台时，需要刷新平台map
     */
    public void platformMapRefresh() {
        platformMap.clear();
        List<MsgPlatform> msgPlatforms =
                msgPlatformDao.selectByExample(new MsgPlatformCriteria());
        for (MsgPlatform msgPlatform : msgPlatforms) {
            if (msgPlatform.getPlatformCode() != null) {
                platformMap.put(msgPlatform.getPlatformCode(), msgPlatform.getId());
            }
        }
    }

    /**
     * 添加新的平台信息
     * @param platformName 平台名称
     * @param platformCode 平台编码
     */
    public void addPlatform(String platformName, Integer platformCode) {
        MsgPlatformCriteria criteria = new MsgPlatformCriteria();
        criteria.createCriteria()
                .andPlatformCodeEqualTo(platformCode.byteValue());
        List<MsgPlatform> msgPlatforms = msgPlatformDao.selectByExample(criteria);
        if (!msgPlatforms.isEmpty()) {
            throw new RuntimeException("平台code不可以重复");
        }
        MsgPlatform platform = new MsgPlatform();
        platform.setName(platformName);
        platform.setPlatformCode(platformCode);
        platform.setEnable(true);
        msgPlatformDao.insert(platform);
        platformMapRefresh();
    }

}
