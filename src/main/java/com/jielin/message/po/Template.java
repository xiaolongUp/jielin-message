package com.jielin.message.po;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
@Document(collection = "jl_msg_template")
@Accessors(chain = true)
public class Template implements Serializable {

    private static final long serialVersionUID = 1L;

    private String _id;

    //模版id，包括短信的模版，公众号模版，小程序模版等id
    private String tmpId;

    //模版名称
    private String tmpName;

    //操作类型
    private Integer operateType;

    //推送类型
    private Integer optionType;

    //创建时间
    private Date createTime;

    //更新时间
    private Date updateTime;

    //是否启用
    private Boolean enable = true;

    //事例
    private String example;

    //公众号和app拥有title（为公众号当中的first）
    private String title;

    private String remark;

    //小程序和公众号字段信息
    private Map<String,String> paramMap;

    //小程序和公众号的跳转地址，小程序中该参数为page，公众号当中该参数为url
    private String jumpAddress;
}