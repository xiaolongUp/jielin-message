package com.jielin.message.po;

import lombok.Data;

import java.io.Serializable;

@Data
public class MsgPushPo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    //名称
    private String name;

    //操作类型(详情OperateTypeEnum)
    private Integer operateType;

    //推送类型(详情PushTypeEnum)
    private Integer optionValue;

}