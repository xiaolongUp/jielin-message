package com.jielin.message.util;

import com.google.gson.Gson;
import com.jielin.message.dao.mysql.MsgSendResultDao;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.po.MsgSendResultPo;
import com.jielin.message.util.constant.MsgConstant;
import com.jielin.message.util.enums.SendMsgResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.connection.PublisherCallbackChannel;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 发送消息并异步监听 ack
 */
@Component
@Slf4j
public class MessageSendAsync implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    private static final String CORRELATION_ID = PublisherCallbackChannel.RETURNED_MESSAGE_CORRELATION_KEY;

    private static final SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);

    private RabbitTemplate rabbitTemplate;

    private MsgSendResultDao msgSendResultDao;

    private Gson gson;

    /**
     * 通过构造函数注入 RabbitTemplate 依赖
     *
     * @param rabbitTemplate rabbitmq
     */
    @Autowired
    public MessageSendAsync(RabbitTemplate rabbitTemplate, MsgSendResultDao msgSendResultDao, Gson gson) {
        this.rabbitTemplate = rabbitTemplate;
        this.msgSendResultDao = msgSendResultDao;
        this.gson = gson;
        // 设置消息到达 exchange 时，要回调的方法，每个 RabbitTemplate 只支持一个 ConfirmCallback
        rabbitTemplate.setConfirmCallback(this);
        // 设置消息无法到达 queue 时，要回调的方法
        rabbitTemplate.setReturnCallback(this);
    }

    /**
     * 发送消息
     *
     * @param exchange   交换机
     * @param routingKey 路由建
     * @param message    消息实体
     */
    public void sendMsg(String exchange, String routingKey, Object message) {
        // 构造包含消息的唯一id的对象，id 必须在该 channel 中始终唯一
        String uid = String.valueOf(idWorker.nextId());
        CorrelationData correlationData = new CorrelationData(uid);
        log.info("ID为: {}", correlationData.getId());
        MsgSendResultPo po = new MsgSendResultPo();
        po.setCorrelationId(uid);
        po.setContent(gson.toJson(message));
        po.setResult(SendMsgResultEnum.SENDING.getStatus());
        po.setFailNum(0);
        msgSendResultDao.insert(po);
        // 完成 数据落库，消息状态打标后，就可以安心发送 message
        rabbitTemplate.convertAndSend(exchange, routingKey, message, correlationData);
    }


    /**
     * 异步监听 消息是否到达 exchange
     *
     * @param correlationData 包含消息的唯一标识的对象
     * @param ack             true 标识 ack，false 标识 nack
     * @param cause           nack 的原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String correlationDataId = correlationData.getId();
        MsgSendResultPo resultPo = msgSendResultDao.selectByCorrelationId(correlationDataId);
        if (ack) {
            log.info("消息投递成功,ID为: {}", correlationDataId);
            msgSendResultDao.updateStatus(SendMsgResultEnum.SUCCESS.getStatus(), correlationDataId, 0);
        } else {
            log.error("消息投递失败,ID为: {},错误信息: {}", correlationDataId, cause);

            Integer failNum = resultPo.getFailNum();
            //最多重试投递三次，超过三次后投递到死信队列做后续处理
            if (failNum >= 3) {
                rabbitTemplate.convertAndSend("", MsgConstant.FAIL_PUSH_MSG, gson.fromJson(resultPo.getContent(), ParamDto.class), correlationData);
            }
            //重试投递
            else {
                rabbitTemplate.convertAndSend("", MsgConstant.RETRY_PUSH_MSG, gson.fromJson(resultPo.getContent(), ParamDto.class), correlationData);
                msgSendResultDao.updateStatus(SendMsgResultEnum.FAIL.getStatus(), correlationDataId, failNum + 1);
            }

        }
    }

    /**
     * 异步监听 消息是否到达 queue
     * 触发回调的条件有两个：1.消息已经到达了 exchange 2.消息无法到达 queue (比如 exchange 找不到跟 routingKey 对应的 queue)
     *
     * @param message    返回的消息
     * @param replyCode  回复 code
     * @param replyText  回复 text
     * @param exchange   交换机
     * @param routingKey 路由键
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        // correlationId 就是发消息时设置的 id
        String correlationId = (String) message.getMessageProperties().getHeaders().get(MsgConstant.CORRELATION_ID);
        MsgSendResultPo resultPo = msgSendResultDao.selectByCorrelationId(correlationId);
        log.error("没有找到对应队列，消息投递失败,ID为: {}, replyCode {} , replyText {}, exchange {} routingKey {}",
                correlationId, replyCode, replyText, exchange, routingKey);
        msgSendResultDao.updateStatus(SendMsgResultEnum.FAIL.getStatus(),
                (String) message.getMessageProperties().getHeaders().get(CORRELATION_ID), resultPo.getFailNum() + 1);
    }


}

