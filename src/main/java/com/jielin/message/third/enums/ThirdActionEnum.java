package com.jielin.message.third.enums;

import org.springframework.http.HttpMethod;

public enum ThirdActionEnum {
    JL_WEB_ACCESS_TOKEN("/service/login", HttpMethod.POST, "获取token"),
    JL_WEB_ADMIN_USER("/thirdApp/adminUser", HttpMethod.GET, "获取登录用户"),
    JL_WEB_ADMIN_ROLE("/thirdApp/adminRole", HttpMethod.GET, "获取登录用户权限"),
    JL_WEB_CUSTOM_USER("/thirdApp/customUser", HttpMethod.GET, "获取客户信息"),
    JL_WEB_SERVICE_USER("/thirdApp/serviceUser", HttpMethod.GET, "获取悦姐信息"),
    JL_WEB_AUTH_MEMBER("/thirdApp/authMember", HttpMethod.GET, "获取悦姐信息")
    ;


    ThirdActionEnum(String actionName, HttpMethod requestType, String desc) {
        this.actionName = actionName;
        this.requestType = requestType;
        this.desc = desc;
    }

    /**
     * 动作
     */
    private String actionName;

    /**
     * 请求方式
     */
    private HttpMethod requestType;
    /**
     * 描述
     */
    private String desc;

    public String getActionName() {
        return actionName;
    }

    public ThirdActionEnum setActionName(String actionName) {
        this.actionName = actionName;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public ThirdActionEnum setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public HttpMethod getRequestType() {
        return requestType;
    }

    public ThirdActionEnum setRequestType(HttpMethod requestType) {
        this.requestType = requestType;
        return this;
    }
}
