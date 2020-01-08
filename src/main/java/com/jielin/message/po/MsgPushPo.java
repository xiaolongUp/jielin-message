package com.jielin.message.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class MsgPushPo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    //描述
    private String dsc;

    //操作类型
    private Integer operateType;

    //推送类型
    private Integer optionValue;

    //平台类型
    private Integer platform;

    //用户类型
    private String userType;

    //优先级
    private Integer priority;

    //是否启用
    private Boolean enable;

    //当前推送成功后下一个推送规则是否需要继续
    private Boolean nextStop;

}