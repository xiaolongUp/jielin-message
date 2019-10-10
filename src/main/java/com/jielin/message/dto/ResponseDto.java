package com.jielin.message.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 通用的返回对象
 *
 * @author yxl
 */
@Data
@Accessors(chain = true)
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

    /**
     * 不带参数的正确的返回结果
     */
    public ResponseDto() {
        this.code = 0;
    }

    /**
     * 错误返回的结果
     *
     * @param message 错误信息描述
     */
    public ResponseDto(String message) {
        this.code = -1;
        this.message = message;
    }

    /**
     * 带参数正确的返回结果
     *
     * @param data 需要返回的数据
     */
    public ResponseDto(T data) {
        this.code = 0;
        this.data = data;
    }

    public ResponseDto(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
