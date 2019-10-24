package com.jielin.message.synpush.UniPush;

import com.gexin.rp.sdk.base.IAliasResult;
import com.gexin.rp.sdk.http.IGtPush;
import com.jielin.message.config.AppPushConfig;
import com.jielin.message.config.UniPushConfig;
import com.jielin.message.dao.mysql.GtAliasDao;
import com.jielin.message.po.GtAliasPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;


/**
 * 别名管理
 *
 * @author yxl
 */
@Service
@Slf4j
public class UniPushAliasHandler {

    @Autowired
    private AppPushConfig config;

    @Autowired
    private GtAliasDao gtAliasDao;

    private Map<String, IGtPush> IGtPushMap = new HashMap<>();

    @PostConstruct
    private void init() {
        //初始化所有的uniapp的client
        for (Map.Entry<String, UniPushConfig.UniPush> uniPushEntry : config.getUniPush().getUniPushMap().entrySet()) {
            IGtPush iGtPush = new IGtPush(uniPushEntry.getValue().getAppKey(), uniPushEntry.getValue().getMasterSecret());
            this.IGtPushMap.put(uniPushEntry.getKey(), iGtPush);
        }
    }

    /**
     * 单个clientid绑定别名, 一个clientid只能绑定一个别名，若已绑定过别名的clientid再次绑定新别名，则认为与前一个别名自动解绑，绑定新别名。
     */
    public Boolean bindSingleAlias(GtAliasPo gtAliasPo) {
        IGtPush pushClient = this.IGtPushMap.get(gtAliasPo.getAppType());
        if (!Optional.ofNullable(pushClient).isPresent()) {
            return false;
        }
        UniPushConfig.UniPush uniPush = config.getUniPush().getUniPushMap().get(gtAliasPo.getAppType());
        if (!Optional.ofNullable(uniPush).isPresent()) {
            return false;
        }
        String appId = uniPush.getAppId();
        IAliasResult ret = pushClient.bindAlias(appId, gtAliasPo.getAlias(), gtAliasPo.getCid());
        List<GtAliasPo> gtAliasPos = gtAliasDao.selectByCid(gtAliasPo.getCid());
        if (gtAliasPos.size() == 0) {
            //别名形式为app类型+phone
            gtAliasPo.setAlias(gtAliasPo.getAppType() + gtAliasPo.getPhone());
            gtAliasDao.insert(gtAliasPo);
        }
        log.info("绑定结果：" + ret.getResponse().toString());
        return ret.getResponse().get("result").equals("ok");
    }
/*

    //多个clientid绑定别名, 允许将多个clientid和一个别名绑定，如用户使用多终端，则可将多终端对应的clientid绑定为一个别名，目前一个别名最多支持绑定10个clientid。
    public void bindListAlias() {
        List<Target> Lcids = new ArrayList<>();
        Target target1 = new Target();
        Target target2 = new Target();
        */
/*target1.setClientId(CID);
        target1.setAlias(ALIAS);
        target2.setClientId(CID_2);
        target2.setAlias(ALIAS_2);*//*

        Lcids.add(target1);
        Lcids.add(target2);

        IAliasResult ret = pushClient.bindAlias(config.getUniPush().getAppId(), Lcids);
        System.out.println("绑定结果：" + ret.getResponse());
    }

     // 根据别名获取clientid信息
    public void queryClientIdsByAlias(String alias) {
        IAliasResult ret = pushClient.queryClientId(config.getUniPush().getAppId(), alias);
        System.out.println(ret.getResponse());
        System.out.println("根据别名获取的CID：" + ret.getResponse().get("cidlist"));
    }

     // 通过clientid获取别名信息
    public void queryAliasByCClientId(String cid) {
        IAliasResult ret = pushClient.queryAlias(config.getUniPush().getAppId(), cid);
        System.out.println(ret.getResponse());
        System.out.println("根据CID获取的别名：" + ret.getResponse().get("alias"));
    }

     // 单个clientid和别名解绑
    public void unBindAlias(String alias, String cid) {
        IAliasResult ret = pushClient.unBindAlias(config.getUniPush().getAppId(), alias, cid);
        System.out.println("解除绑定结果:" + ret.getResponse());
    }

     // 绑定别名的所有clientid解绑
    public void unBindAliasAll(String alias) {
        IAliasResult ret = pushClient.unBindAliasAll(config.getUniPush().getAppId(), alias);
        System.out.println("解除绑定结果:" + ret.getResponse());
    }
*/

    /**
     * @param cid 个推cid
     * @return 数据库是否存在该设备的绑定关系
     */
    public List<GtAliasPo> hasBindAlias(String cid) {
        return gtAliasDao.selectByCid(cid);
    }

}
