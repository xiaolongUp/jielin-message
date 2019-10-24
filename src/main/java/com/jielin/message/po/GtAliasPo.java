package com.jielin.message.po;

import lombok.Data;

import java.io.Serializable;

@Data
public class GtAliasPo implements Serializable {

    private Integer id;

    //个推cid
    private String cid;

    //别名
    private String alias;

    //app类型。悦姐app,客户app,悦所等
    private String appType;

    //电话号码
    private String phone;

    private static final long serialVersionUID = 1L;

}