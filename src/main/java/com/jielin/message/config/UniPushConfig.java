package com.jielin.message.config;

import com.jielin.message.dao.mysql.UniappDao;
import com.jielin.message.po.UniappPo;
import com.jielin.message.po.UniappPoCriteria;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 个推推送
 *
 * @author yxl
 */
@Component
@Getter
@Setter
public class UniPushConfig {

    @Autowired
    private UniappDao uniappDao;

    //所有的集成个推的配置类
    private Map<String, UniPush> uniPushMap = new HashMap<>();

    //初始化所有的配置文件，从数据当中加载
    @PostConstruct
    public void init() {
        List<UniappPo> uniappPos = uniappDao.selectByExample(new UniappPoCriteria());
        for (UniappPo uniappPo : uniappPos) {
            UniPush uniPush = this.getUniPush(uniappPo);
            this.uniPushMap.put(uniappPo.getAppType().concat(uniappPo.getPlatform().toString()), uniPush);
        }
    }

    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    public class UniPush {

        private String appId;

        private String appSecret;

        private String appKey;

        private String masterSecret;
    }

    private UniPush getUniPush(UniappPo uniappPo) {
        UniPush uniPush = new UniPush();
        uniPush.appId = uniappPo.getAppId();
        uniPush.appSecret = uniappPo.getAppSecret();
        uniPush.appKey = uniappPo.getAppKey();
        uniPush.masterSecret = uniappPo.getMasterSecret();
        return uniPush;
    }

}
