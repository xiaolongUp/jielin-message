package com.jielin.message.po;

import lombok.Data;

import java.io.Serializable;

@Data
public class MenuPo implements Serializable {

    private Integer id;

    //名称
    private String name;

    //展示图标
    private String icon;

    //访问地址
    private String url;

    //父节点
    private Integer parent;

    private static final long serialVersionUID = 1L;

}