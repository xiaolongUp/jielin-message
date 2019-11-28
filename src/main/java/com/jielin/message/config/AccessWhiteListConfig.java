package com.jielin.message.config;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 同步推送调用接口时的白名单限制
 *
 * @author yxl
 */
@Configuration
@ConfigurationProperties(prefix = "white")
@Getter
public class AccessWhiteListConfig {

    private List<String> access = new ArrayList<>();

    //查看调用该接口的ip是否有权限调用
    public final boolean canAccess(String remoteIp) {
        if (StringUtils.isBlank(remoteIp)) {
            return false;
        }
        for (String ip : this.access) {
            if (remoteIp.equals(ip)) {
                return true;
            }
        }
        return false;
    }
}
