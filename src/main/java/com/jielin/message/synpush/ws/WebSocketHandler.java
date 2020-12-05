package com.jielin.message.synpush.ws;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.po.OperatePo;
import com.jielin.message.synpush.MsgPush;
import com.jielin.message.util.RedisUtil;
import com.jielin.message.util.constant.WsConstant;
import com.jielin.message.util.enums.WsDataEnum;
import com.jielin.message.websocket.WebSocketService;
import com.jielin.message.websocket.config.WebSocketSessionListener;
import com.jielin.message.websocket.pojo.AdminAlertContent;
import com.jielin.message.websocket.pojo.AdminHeaderContent;
import com.jielin.message.websocket.pojo.SocketResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.jielin.message.util.constant.WsConstant.*;
import static com.jielin.message.util.enums.PushTypeEnum.WEBSOCKET_PUSH;


/**
 * 通过websocket 向浏览器推送消息
 */
@Component
@Slf4j
public class WebSocketHandler extends MsgPush {

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private Gson gson = new GsonBuilder().serializeNulls().create();

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private WebSocketSessionListener listener;

    @Override
    public boolean pushMsg(ParamDto paramDto, OperatePo operatePo) throws Exception {
        super.setThreadLocalParams(paramDto, operatePo, WEBSOCKET_PUSH);
        Map<String, Object> params = paramDto.getParams();
        String userName = (String) params.get(WsConstant.WEBSOCKET_USER);
        if (!(params.get(TYPE) instanceof Integer)) {
            super.insertMsgSendLog(paramDto, operatePo.getOperateName(), WEBSOCKET_PUSH, false, "参数有误!");
            return false;
        }
        Integer type = (Integer) params.get(TYPE);
        //固定值，0代表推送头部消息内容
        if (type.equals(WsDataEnum.ADMIN_DATA_HEADER.getType())) {
            AdminHeaderContent content = gson.fromJson((String) params.get("content"), AdminHeaderContent.class);
            webSocketService.send2User(userName, new SocketResponse<>(content));
            redisUtil.set(HEADER_PREFIX + userName, content);
        }
        //固定值，1代表推送请假弹出框的内容
        else if (type.equals(WsDataEnum.ADMIN_DATA_ALERT.getType())) {
            AdminAlertContent content = gson.fromJson((String) params.get("content"), AdminAlertContent.class);
            //弹窗消息在线时直接发送，当离线时需要缓存起来，等用户上线后再次发送
            if (listener.getOnLineUsers().contains(userName)) {
                webSocketService.send2User(userName, new SocketResponse<>(content));
            } else {
                List<Object> list = redisUtil.listGet(ALERT_PREFIX + userName);
                list.add(content);
                redisUtil.del(ALERT_PREFIX + userName);
                redisUtil.listAddAll(ALERT_PREFIX + userName, list);
            }
        } else {
            return false;
        }
        super.insertMsgSendLog(paramDto, operatePo.getOperateName(), WEBSOCKET_PUSH, true, "websocket推送成功");
        return true;
    }

    @Override
    public boolean supports(Integer handlerType) {
        return WEBSOCKET_PUSH.getType() == handlerType;
    }
}
