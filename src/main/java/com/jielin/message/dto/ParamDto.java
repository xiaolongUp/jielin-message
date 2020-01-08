package com.jielin.message.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

/**
 * 推送消息发送时需要的参数
 *
 * @author yxl
 */
@Data
@Accessors(chain = true)
public class ParamDto implements Serializable {

    private static final long serialVersionUID = 1L;

    //用户id
    @NotNull(message = "用户id不能为空")
    private Integer userId;

    //来自哪个平台（悦管家平台，悦所平台等）
    @NotNull(message = "所属平台不能为空，默认悦管家平台为0")
    private Integer platform;

    //需要发送的推送的类型（OperateTypeEnum）
    @NotNull(message = "操作类型不能为空")
    private Integer operateType;

    //需要推送的数据到哪个手机号
    @NotBlank(message = "用户手机号不能为空")
    private String phoneNumber;

    //用户类型
    @NotBlank(message = "用户类型不能为空，悦管家平台悦姐：provider，用户：customer")
    private String userType;

    //需要的参数
    private Map<String, Object> params;

    //钉钉推送需要的数据
    private DingMsg dingMsg;

    public ParamDto(){
        this.dingMsg = new DingMsg();
    }

    @Data
    @NoArgsConstructor
    public class DingMsg {

        private String DingUserId;

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

}
