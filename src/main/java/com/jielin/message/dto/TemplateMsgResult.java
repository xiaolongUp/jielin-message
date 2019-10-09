package com.jielin.message.dto;

import lombok.Data;

/**
 * 微信公众号发送模版消息返回结果
 *
 * @author yxl
 */
@Data
public class TemplateMsgResult {

    // 消息id(发送模板消息)
    private String msgid;

    //返回状态码 0为成功
    private int errcode;

    //错误信息
    private String errmsg;
}
