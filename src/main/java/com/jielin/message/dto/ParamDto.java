package com.jielin.message.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * 推送消息发送时需要的参数
 *
 * @author yxl
 */
@Data
@Accessors(chain = true)
public class ParamDto  {

    //需要发送的推送的类型（OperateTypeEnum）
    private Integer operateType;

    //需要推送的数据到哪个手机号
    private String phoneNumber;

    //需要推送的app
    private String appType;

    //需要的参数
    private Map<String,Object> params;

}
