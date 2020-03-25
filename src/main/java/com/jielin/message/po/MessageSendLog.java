package com.jielin.message.po;

import com.jielin.message.dto.ParamDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * 推送的结果日志
 *
 * @author yxl
 */
@Data
@Accessors(chain = true)
@Document(collection = "jl_message_send_log")
@NoArgsConstructor
public class MessageSendLog implements Serializable {

    private static final long serialVersionUID = 1L;

    //唯一标识
    private String _id;

    //业务系统的消息唯一标实,用来查询消息是否投递成功
    private String msgId;

    //消息的唯一标识
    private String correlationId;

    //插入时间
    private Date operateTime = new Date();

    //用户id（悦姐或者用户,或者钉钉用户id）
    private String userId;

    //接收人手机号
    private String phone;

    //操作类型
    private String operateType;

    //平台
    private String platform;

    //推送类型
    private String pushType;

    //推送是否成功
    private Boolean result;

    //推送结果
    private String resultMsg;

    public MessageSendLog(ParamDto paramDto, String operateType, String pushType, String resultMsg) {
        this.msgId = paramDto.getMsgId();
        this.correlationId = paramDto.getCorrelationId();
        if (null != paramDto.getUserId()) {
            this.userId = paramDto.getUserId().toString();
        }
        this.phone = paramDto.getPhoneNumber();
        this.operateType = operateType;
        this.pushType = pushType;
        this.resultMsg = resultMsg;
    }

    public MessageSendLog(ParamDto paramDto, String operateType, String pushType, Boolean result, String resultMsg) {
        this(paramDto, operateType, pushType, resultMsg);
        if (null != paramDto.getPlatform()) {
            this.platform = paramDto.getPlatform().toString();
        }
        this.result = result;
    }
}
