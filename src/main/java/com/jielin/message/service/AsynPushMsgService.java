package com.jielin.message.service;

import com.jielin.message.dao.mysql.MsgSendResultDao;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.po.MsgSendResultPo;
import com.jielin.message.util.constant.MsgConstant;
import com.jielin.message.util.enums.SendMsgResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    @RabbitListener(queues = {MsgConstant.PUSH_MSG, MsgConstant.RETRY_PUSH_MSG})
    public void process(ParamDto paramDto, Message message) {
        //获取correlationDataId
        String correlationDataId =
                (String) message.getMessageProperties().getHeaders().get(MsgConstant.CORRELATION_ID);
        MsgSendResultPo resultPo = msgSendResultDao.selectByCorrelationId(correlationDataId);
        if (!SendMsgResultEnum.SUCCESS.getStatus().equals(resultPo.getResult())) {
            synMsgPushService.push(paramDto);
            msgSendResultDao.updateStatus(SendMsgResultEnum.SUCCESS.getStatus(), correlationDataId, resultPo.getFailNum());
        }
        log.info("Receiver  : {}", paramDto.toString());
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
