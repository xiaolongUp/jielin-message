package com.jielin.message.service;

import com.jielin.message.dao.mysql.MsgSendResultDao;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.po.MsgSendResultPo;
import com.jielin.message.util.constant.MsgConstant;
import com.jielin.message.util.enums.SendMsgResultEnum;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 异步的推送消息
 *
 * @author yxl
 */
@Service
@Slf4j
public class AsynPushMsgService {

    @Autowired
    private SynMsgPushService synMsgPushService;

    @Autowired
    private MsgSendResultDao msgSendResultDao;

    //监听消息队列当中的数据
    @RabbitHandler
    @RabbitListener(queues = MsgConstant.PUSH_MSG)
    public void process(ParamDto paramDto, Channel channel, Message message) {
        log.error("收到消息--------------------：{}", paramDto.toString());
        //获取correlationDataId
        String correlationDataId =
                (String) message.getMessageProperties().getHeaders().get(MsgConstant.CORRELATION_ID);
        MsgSendResultPo resultPo = msgSendResultDao.selectByCorrelationId(correlationDataId);
        try {
            if (!SendMsgResultEnum.SUCCESS.getStatus().equals(resultPo.getResult())) {
                paramDto.setCorrelationId(correlationDataId);
                synMsgPushService.push(paramDto);
                msgSendResultDao.updateStatus(SendMsgResultEnum.SUCCESS.getStatus(), correlationDataId, resultPo.getFailNum());
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
            log.info("Receiver  : {}", paramDto.toString());
        } catch (IOException e) {
            //丢弃这条消息
            //channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,false);
            log.error("receiver fail,correlation_id = {}", correlationDataId);
        }
    }

    //监听消息队重试的消息
    @RabbitHandler
    @RabbitListener(queues = MsgConstant.RETRY_PUSH_MSG)
    public void processRetry(ParamDto paramDto, Channel channel, Message message) {
        //获取correlationDataId
        String correlationDataId =
                (String) message.getMessageProperties().getHeaders().get(MsgConstant.CORRELATION_ID);
        MsgSendResultPo resultPo = msgSendResultDao.selectByCorrelationId(correlationDataId);
        try {
            if (resultPo == null) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }
            if (!SendMsgResultEnum.SUCCESS.getStatus().equals(resultPo.getResult()) && resultPo.getFailNum() > 0) {
                paramDto.setCorrelationId(correlationDataId);
                synMsgPushService.push(paramDto);
                msgSendResultDao.updateStatus(SendMsgResultEnum.SUCCESS.getStatus(), correlationDataId, resultPo.getFailNum());
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
            log.info("Receiver  : {}", paramDto.toString());
        } catch (IOException e) {
            //丢弃这条消息
            //channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,false);
            //丢弃这一条错误消息
            try {
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                log.error("receiver fail,correlation_id = {}", correlationDataId);
            } catch (IOException ex) {
                log.error("correlationDataId：{},丢弃消息失败！", correlationDataId);
            }

        }
    }

    //失败消息队列
    @RabbitHandler
    @RabbitListener(queues = MsgConstant.FAIL_PUSH_MSG)
    public void processFail(ParamDto paramDto, Message message) {
        //获取correlationDataId
        String correlationDataId =
                (String) message.getMessageProperties().getHeaders().get(MsgConstant.CORRELATION_ID);
        //todo 最终数据入库发送邮件通知管理员等
    }

}
