package com.jielin.message.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 通用的返回对象
 *
 * @author yxl
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@SuppressWarnings("unused")
public class ResponseDto<T> {

    /**
     * 状态，0 表示成功
     */
    private int code;

    /**
     * 错误信息
     */
    private String message;

    /**
     * 业务信息
     */
    private T data;

    public ResponseDto(String message) {
        this.code = 0;
        this.message = message;
    }

    public ResponseDto(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
