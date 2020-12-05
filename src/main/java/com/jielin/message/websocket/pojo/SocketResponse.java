package com.jielin.message.websocket.pojo;

public class SocketResponse<T> {
    private T responseMessage;

    public SocketResponse(T responseMessage) {
        this.responseMessage = responseMessage;
    }

    public T getResponseMessage() {
        return responseMessage;
    }
}
