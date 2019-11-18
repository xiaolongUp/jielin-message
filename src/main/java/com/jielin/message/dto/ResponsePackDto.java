package com.jielin.message.dto;

public class ResponsePackDto {

    private Integer status;
    private String error;
    private Object body;

    public ResponsePackDto() {
        this.status = 0;
        this.error = "";
        this.body = null;
    }

    /**
     * 返回结果状态，如果没有异常则读取body，如果有异常则读取error字段，查看具体问题。
     * @return
     */
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 错误原因
     * @return
     */
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    /**
     * 返回的详细内容，如果成功返回则读取这个字段。
     * @return
     */
    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "ResponsePackDto{" +
                "status=" + status +
                ", error='" + error + '\'' +
                ", body=" + body +
                '}';
    }
}
