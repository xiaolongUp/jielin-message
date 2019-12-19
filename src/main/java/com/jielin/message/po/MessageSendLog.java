package com.jielin.message.po;

import com.jielin.message.dto.ParamDto;
import com.jielin.message.util.enums.OperateTypeEnum;
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

    //插入时间
    private Date operateTime = new Date();

    //用户id（悦姐或者用户,或者钉钉用户id）
    private String userId;

    //接收人手机号
    private String phone;

    //操作类型
    private String operateType;

    //推送类型
    private String pushType;

    //params
    private String params;

    //推送结果
    private String result;

    public MessageSendLog(ParamDto paramDto,String pushType, String result) {
        this.userId = paramDto.getUserId().toString();
        this.phone = paramDto.getPhoneNumber();
        this.operateType = OperateTypeEnum.getDescByType(paramDto.getOperateType());
        this.pushType = pushType;
        this.params = paramDto.toString();
        this.result = result;
    }
}
