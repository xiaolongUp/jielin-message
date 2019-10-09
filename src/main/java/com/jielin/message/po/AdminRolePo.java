package com.jielin.message.po;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录用户角色
 *
 * @author yxl
 */
@Data
public class AdminRolePo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String name;

    private String description;

    private Integer parent;

    private Boolean enabled;

    private Integer priority;

    private Integer sortOrder;

    public AdminRolePo() {
        this.name = "";
        this.description = "";
        this.parent = 0;
        this.enabled = true;
        this.priority = 0;
        this.sortOrder = 0;
    }
}