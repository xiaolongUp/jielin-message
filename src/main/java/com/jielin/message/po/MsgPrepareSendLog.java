package com.jielin.message.po;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
@Document(collection = "jl_msg_prepare_send_log")
@NoArgsConstructor
public class MsgPrepareSendLog implements Serializable {
    private static final long serialVersionUID = 1L;

    //唯一标识
    private String _id;

    //消息的唯一标识
    private String correlationId;

    //插入时间
    private Date operateTime = new Date();

    //操作类型
    private Integer operateType;

    //推送类型
    private List<Integer> pushTypeList;

    //推送参数
    private String params;

    //其他信息
    private String msg;
}
