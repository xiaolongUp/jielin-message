package com.jielin.message.websocket;

import com.jielin.message.websocket.pojo.SocketResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate template;

    /**
     * 广播
     * 发给所有在线用户
     */
    public void sendMsg(SocketResponse msg) {
        template.convertAndSend("/user/240/msg", msg);
    }

    /**
     * 发送给指定用户组
     *
     * @param users 用户列表
     * @param msg   消息
     */
    public void send2Users(List<String> users, SocketResponse msg) {
        users.forEach(userName -> template.convertAndSendToUser(userName, "/msg", msg));
    }

    /**
     * 给指定的单一用户发送消息
     *
     * @param user 用户名称
     * @param msg  消息体
     */
    public void send2User(String user, SocketResponse msg) {
        template.convertAndSendToUser(user, "/msg", msg);
    }
}
