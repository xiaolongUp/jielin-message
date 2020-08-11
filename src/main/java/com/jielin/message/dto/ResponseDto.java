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
    private int status;

    /**
     * 错误信息
     */
    private String error;

    /**
     * 业务信息
     */
    private T body;

    /**
     * 不带参数的正确的返回结果
     */
    public ResponseDto() {
        this.status = 0;
    }


    /**
     * 错误返回的结果
     *
     * @param error 错误信息描述
     */
    public ResponseDto(String error) {
        this.status = -1;
        this.error = error;
    }

    /**
     * 带参数正确的返回结果
     *
     * @param body 需要返回的数据
     */
    public ResponseDto(T body) {
        this.status = 0;
        this.body = body;
    }

    public ResponseDto(int status, String error) {
        this.status = status;
        this.error = error;
    }

    public static <T> ResponseDto success(T body) {
        ResponseDto<T> dto = new ResponseDto<>();
        dto.status = 0;
        dto.setBody(body);
        return dto;
    }

    public static ResponseDto success() {
        ResponseDto dto = new ResponseDto();
        dto.status = 0;
        return dto;
    }

    public static ResponseDto fail(String error) {
        ResponseDto dto = new ResponseDto();
        dto.status = -1;
        dto.setError(error);
        return dto;
    }
}
