package com.jielin.message.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 登录用户
 *
 * @author yxl
 */
@Data
@NoArgsConstructor
public class AdminUserPo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String username;

    private String email;

    private String password;

    private Date createTime;

    private Date lastLoginTime;

    private String lastLoginIp;

    private Integer role;

    private Byte status;

    private Integer org;

    private String lastLoginLocation;

    private String remark;

    private String phone;

    private String province;

    private String city;

    private String district;

    private Boolean enabled;

    private Date updateTime;


}