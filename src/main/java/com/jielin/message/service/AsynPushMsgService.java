package com.jielin.message.service;

import com.jielin.message.dao.mysql.MsgSendResultDao;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.po.MsgSendResultPo;
import com.jielin.message.util.MessageSendAsync;
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

    @Autowired
    private MessageSendAsync messageSendAsync;

    //两个小时
    public static final int twoHours = 7200000;

    //监听消息队列当中的数据
    @RabbitHandler
    @RabbitListener(queues = MsgConstant.PUSH_MSG)
    public void process(ParamDto paramDto, Channel channel, Message message) throws IOException {
        //获取correlationDataId
        String correlationDataId =
                (String) message.getMessageProperties().getHeaders().get(MsgConstant.CORRELATION_ID);
        MsgSendResultPo resultPo = msgSendResultDao.selectByCorrelationId(correlationDataId);
        //当数据不存在时，说明该数据为无效数据，直接丢弃该数据
        if (resultPo == null) {
            log.error("-----消费数据未入库异常-----{}", message.toString());
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        //当消息入队时间大于配置时间，舍弃掉不消费，不入队
        if (paramDto.getQueuedTime() != null && (currentTimeMillis - paramDto.getQueuedTime()) > twoHours) {
            log.error("-----消费数据入队时间超过两个小时，舍弃-----{}", message.toString());
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }
        try {
            if (!SendMsgResultEnum.SUCCESS.getStatus().equals(resultPo.getResult())) {
                paramDto.setCorrelationId(correlationDataId);
                synMsgPushService.push(paramDto);
                msgSendResultDao.updateStatus(SendMsgResultEnum.SUCCESS.getStatus(), correlationDataId, resultPo.getFailNum());
            }
            log.info("Receiver  : {}", paramDto.toString());
        } catch (Exception e) {
            log.error("----数据消费异常----", e);
            //消费失败的条件下，投递到重试队列再次尝试消费，此处没有多次重试
            messageSendAsync.sendMsg("", MsgConstant.RETRY_PUSH_MSG, paramDto, correlationDataId);
            msgSendResultDao.updateStatus(SendMsgResultEnum.CONSUME_FAIL.getStatus(), correlationDataId, resultPo.getFailNum() == null ? 0 : resultPo.getFailNum() + 1);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    //监听消息队重试的消息
    @RabbitHandler
    @RabbitListener(queues = MsgConstant.RETRY_PUSH_MSG)
    public void processRetry(ParamDto paramDto, Channel channel, Message message) throws IOException {
        log.error("----消费失败重新消费数据---{}", message.toString());
        //获取correlationDataId
        String correlationDataId =
                (String) message.getMessageProperties().getHeaders().get(MsgConstant.CORRELATION_ID);
        MsgSendResultPo resultPo = msgSendResultDao.selectByCorrelationId(correlationDataId);
        if (resultPo == null) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            return;
        }
        try {
            if (SendMsgResultEnum.CONSUME_FAIL.getStatus().equals(resultPo.getResult())) {
                paramDto.setCorrelationId(correlationDataId);
                synMsgPushService.push(paramDto);
                msgSendResultDao.updateStatus(SendMsgResultEnum.SUCCESS.getStatus(), correlationDataId, resultPo.getFailNum());
            }
            log.info("Receiver  : {}", paramDto.toString());
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            //直接丢弃到死信队列
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }

    //失败消息队列
    @RabbitHandler
    @RabbitListener(queues = MsgConstant.FAIL_PUSH_MSG)
    public void processFail(ParamDto paramDto, Channel channel, Message message) throws IOException {
        //获取correlationDataId
        String correlationDataId =
                (String) message.getMessageProperties().getHeaders().get(MsgConstant.CORRELATION_ID);
        //死信队列的数据消费直接ack掉，特殊处理的业务逻辑待完善
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        //todo 最终数据入库发送邮件通知管理员等
    }

}
