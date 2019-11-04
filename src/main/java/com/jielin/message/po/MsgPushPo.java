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

    //优先级
    private Integer priority;

    //是否启用
    private Boolean enable;

}