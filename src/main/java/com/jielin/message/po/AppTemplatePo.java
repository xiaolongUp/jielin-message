package com.jielin.message.po;

import lombok.Data;

import java.io.Serializable;

@Data
public class AppTemplatePo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

   //操作类型
    private Integer operateType;

    //推送标题
    private String title;

    //推送内容
    private String content;

}