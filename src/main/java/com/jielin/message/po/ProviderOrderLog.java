package com.jielin.message.po;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Data
@Document(collection = "jl_provider_order")
@Accessors(chain = true)
public class ProviderOrderLog implements Serializable {

    private static final long serialVersionUID = 1L;

    //唯一标识
    private String _id;

    //悦姐手机号
    private String phone;

    //推送方式
    private String pushType;

    //悦姐推送消息
    private String msg;

    //推送时间
    private Date pushTime = new Date();

    public ProviderOrderLog(String phone, String pushType, String msg) {
        this.phone = phone;
        this.pushType = pushType;
        this.msg = msg;
    }

}
