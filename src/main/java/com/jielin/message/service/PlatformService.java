package com.jielin.message.service;

import com.jielin.message.dao.mysql.MsgPlatformDao;
import com.jielin.message.po.MsgPlatform;
import com.jielin.message.po.MsgPlatformCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PlatformService {

    @Autowired
    private MsgPlatformDao msgPlatformDao;

    public static Map<Integer, Integer> platformMap = new HashMap<>();

    /**
     *
     */
    @PostConstruct
    public void init() {
        List<MsgPlatform> msgPlatforms =
                msgPlatformDao.selectByExample(new MsgPlatformCriteria());
        for (MsgPlatform msgPlatform : msgPlatforms) {
            if (msgPlatform.getPlatformCode() != null ){
                platformMap.put(msgPlatform.getPlatformCode(), msgPlatform.getId());
            }
        }
    }


}
