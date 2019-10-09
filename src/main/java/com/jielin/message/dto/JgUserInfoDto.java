package com.jielin.message.dto;

import lombok.Data;

/**
 * 保存的极光对象
 *
 * @author yxl
 */
@Data
public class JgUserInfoDto {

    //注册id
    private String registrationId;

    //手机号
    private String phoneNumber;

    //标签
    private String tags;

    //别名
    private String alias;

    //平台
    private String platform;
}
