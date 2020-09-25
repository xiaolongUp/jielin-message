package com.jielin.message.util.task;


import com.google.gson.Gson;
import com.jielin.message.dao.mysql.MsgSendResultDao;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.po.MsgSendResultPo;
import com.jielin.message.util.constant.MsgConstant;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RetryPushMsgTask {

    @Autowired
    private MsgSendResultDao msgSendResultDao;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Gson gson;

    //网络抖动，数据未消费成功时，定时扫描数据库当中没有投递成功的消息，取出后重新投递（需要时开启即可）
    //@Scheduled(cron = "0 */30 * * * *")
    public void reportCurrentTime() {
        List<MsgSendResultPo> msgSendResultPos = msgSendResultDao.selectMsgPushFail();
        msgSendResultPos.forEach(resultPo -> {
            CorrelationData correlationData = new CorrelationData(resultPo.getCorrelationId());
            rabbitTemplate.convertAndSend("", MsgConstant.RETRY_PUSH_MSG, gson.fromJson(resultPo.getContent(), ParamDto.class), correlationData);
        });
    }
}
