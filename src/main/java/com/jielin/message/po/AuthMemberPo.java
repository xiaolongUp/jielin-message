package com.jielin.message.po;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AuthMemberPo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String openid;

    private String platform;

    private Date updateTime;

    private String token;

    private Date tokenExptime;

    private Integer customId;

    private Byte customType;

    private Date bindTime;

    private String remark;

    private String nickname;

}