package com.jielin.message.dto;

import lombok.Data;
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
    private String userType;

    //各系统生成的唯一消息标实
    private String msgId;

    //消息的唯一标识（消息队列生成的全局唯一）
    private String correlationId;

    //入队时间
    private Long queuedTime;

    //需要的参数
    private Map<String, Object> params;
}
