package com.jielin.message.po;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * 操作日志
 *
 * @author yxl
 */
@Data
@Document(collection = "jl_error_send_log")
@NoArgsConstructor
public class OperateLog implements Serializable {

    private static final long serialVersionUID = 1L;

    //唯一标识
    private String _id;

    private Date operateTime = new Date();

    //错误信息
    private Object obj;

    public OperateLog(Object obj) {
        this.obj = obj;
    }
}
