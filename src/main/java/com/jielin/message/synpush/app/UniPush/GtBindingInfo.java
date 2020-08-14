package com.jielin.message.synpush.app.UniPush;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
public class GtBindingInfo implements Serializable {

    private Integer id;

    //个推cid
    private String cid;

    //别名
    private String alias;

    //app类型。悦姐app,客户app,悦所等
    private String appType;

    //电话号码
    private String phone;

    //是否登录
    private byte isLogin;

    //创建时间
    private Date createTime;

    //更新时间
    private Date updateTime;

}
