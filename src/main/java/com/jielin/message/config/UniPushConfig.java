package com.jielin.message.config;

import com.jielin.message.util.MsgConstant;
import lombok.Getter;
import lombok.Setter;

/**
 * 个推推送
 *
 * @author yxl
 */
@Getter
@Setter
public class UniPushConfig {

    private String providerAppId;

    private String providerAppSecret;

    private String providerAppKey;

    private String providerMasterSecret;

    private String customerAppId;

    private String customerAppSecret;

    private String customerAppKey;

    private String customerMasterSecret;

    private String url;

    /**
     * 根据app类型获取appId
     * @param type app类型
     */
    public String getAppId(String type) {
        switch (type) {
            case MsgConstant.PROVIDER_APP:
                return this.providerAppId;
            case MsgConstant.CUSTOMER_APP:
                return this.customerAppId;
            default:
                return null;
        }
    }

    /**
     * 根据app类型获取appKey
     * @param type app类型
     */
    public String getAppKey(String type) {
        switch (type) {
            case MsgConstant.PROVIDER_APP:
                return this.providerAppKey;
            case MsgConstant.CUSTOMER_APP:
                return this.customerAppKey;
            default:
                return null;
        }
    }

}
