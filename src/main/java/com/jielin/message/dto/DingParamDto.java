package com.jielin.message.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 钉钉推送需要的参数信息
 */
@Data
@Accessors(chain = true)
public class DingParamDto {

    //需要推送的用户
    private String userId;

    //推送的消息类型（文本消息
    //图片消息
    //语音消息
    //文件消息
    //链接消息
    //OA消息
    //markdown消息
    //卡片消息）
    private String dingMsgType;

    //推送的消息内容
    private String dingMsgContent;

}
