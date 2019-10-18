package com.jielin.message.util.wechat;

import com.jielin.message.config.WeChatConfig;
import com.jielin.message.dto.ParamDto;
import com.jielin.message.dto.WechatTemplateMsg;
import com.jielin.message.util.enums.OperateTypeEnum;
import com.jielin.message.util.sms.DateUtil;

import java.util.Date;

/**
 * 生成公众号推送消息的模版，舍弃类，使用数据库存储对应的模版信息和拼接参数
 *
 * @author yxl
 */
@Deprecated
public class WxTemplateFactory {

    private static final String FIRST = "first";

    private static final String KEYWORD1 = "keyword1";

    private static final String KEYWORD2 = "keyword2";

    private static final String KEYWORD3 = "keyword3";

    private static final String KEYWORD4 = "keyword4";

    private static final String KEYWORD5 = "keyword5";

    private static final String REMARK = "remark";

    public static String newTemplateData(ParamDto paramDto, String toUser) {
        WechatTemplateMsg templateMsg = new WechatTemplateMsg();
        String data = null;
        switch (OperateTypeEnum.valueOf(paramDto.getOperateType())) {
            case CREATE_ORDER_SUCCESS:
                data = templateMsg.setTemplate_id(WeChatConfig.CREATE_ORDER_SUCCESS)
                        .setTouser(toUser)
                        .add(FIRST, "尊敬的客户，您的服务购买成功", "#0000FF")
                        .add(KEYWORD1, paramDto.getOrderMsg().getCustomName(), "#0000FF")
                        .add(KEYWORD2, paramDto.getOrderMsg().getProductName(), "#0000FF")
                        .add(KEYWORD3, paramDto.getOrderMsg().getMoney().toString(), "#0000FF")
                        .add(KEYWORD4, paramDto.getOrderMsg().getServiceTime(), "#0000FF")
                        .add(REMARK, "请您在家等候，感谢配合。联络请致电400-0022-699", "#0000FF")
                        .toJsonStr();
                break;
            case DISPATCH_ORDER_SUCCESS:
                data = templateMsg.setTemplate_id(WeChatConfig.DISPATCH_ORDER_SUCCESS)
                        .setTouser(toUser)
                        .add(FIRST, "亲爱的悦姐，您有一笔新订单待处理", "#0000FF")
                        .add(KEYWORD1, paramDto.getOrderMsg().getProductName(), "#0000FF")
                        .add(KEYWORD2, paramDto.getOrderMsg().getServiceTime(), "#0000FF")
                        .add(KEYWORD3, paramDto.getOrderMsg().getCustomPhone(), "#0000FF")
                        .add(REMARK, "为了不影响您此单的收入，开始服务后请务必在悦姐APP点击“开始服务”按钮。", "#0000FF")
                        .toJsonStr();
                break;
            case DEPART_ORDER:
                data = templateMsg.setTemplate_id(WeChatConfig.DEPART_ORDER)
                        .setTouser(toUser)
                        .add(FIRST, paramDto.getOrderMsg().getGender() + "出发通知", "#0000FF")
                        .add(KEYWORD1, paramDto.getOrderMsg().getServiceUserName(), "#0000FF")
                        .add(KEYWORD2, paramDto.getOrderMsg().getServiceTime(), "#0000FF")
                        .add(KEYWORD3, paramDto.getOrderMsg().getProductName(), "#0000FF")
                        .add(REMARK, "如有问题就给萌小悦400-0022-699来电吧", "#0000FF")
                        .toJsonStr();
                break;
            case ORDER_FINISH:
                data = templateMsg.setTemplate_id(WeChatConfig.ORDER_FINISH)
                        .setTouser(toUser)
                        .add(FIRST, "您已签单成功", "#0000FF")
                        .add(KEYWORD1, "周期清洁", "#0000FF")
                        .add(KEYWORD2, "完成时间", "#0000FF")
                        .add(REMARK, "快来订单详情里写下您的评价吧，可以给悦姐点赞或吐槽呦。", "#0000FF")
                        .toJsonStr();
                break;
            case ASSIST_CREATE_ORDER_SUCCESS:
                data = templateMsg.setTemplate_id(WeChatConfig.ASSIST_CREATE_ORDER_SUCCESS)
                        .setTouser(toUser)
                        .add(FIRST, "服务预约成功通知", "#0000FF")
                        .add(KEYWORD1, "杨晓龙", "#0000FF")
                        .add(KEYWORD2, "1853076638", "#0000FF")
                        .add(KEYWORD3, "日常清洁", "#0000FF")
                        .add(KEYWORD4, DateUtil.dateToStr(new Date()), "#0000FF")
                        .add(KEYWORD5, "100.00", "#0000FF")
                        .add(REMARK, "有问题请致电萌小悦400-0022-699，还可关注微信公众号“悦管家Life”，有惊喜哟。", "#0000FF")
                        .toJsonStr();
                break;
            case GROUP_DEPART_ORDER:
                data = templateMsg.setTemplate_id(WeChatConfig.GROUP_DEPART_ORDER)
                        .setTouser(toUser)
                        .add(FIRST, "购买成功", "#0000FF")
                        .add(KEYWORD1, "杨晓龙", "#0000FF")
                        .add(KEYWORD2, "日常清洁", "#0000FF")
                        .add(KEYWORD3, "100.00", "#0000FF")
                        .add(KEYWORD4, DateUtil.dateToStr(new Date()), "#0000FF")
                        .toJsonStr();
                break;
            case DISPATCH_ORDER_SUCCESS_CUSTOMER:
                data = templateMsg.setTemplate_id(WeChatConfig.DISPATCH_ORDER_SUCCESS_CUSTOMER)
                        .setTouser(toUser)
                        .add(FIRST, "尊敬的客户，您的订单派单成功！", "#0000FF")
                        .add(KEYWORD1, paramDto.getOrderMsg().getProductName(), "#0000FF")
                        .add(KEYWORD2, paramDto.getOrderMsg().getServiceUserName(), "#0000FF")
                        .add(KEYWORD3, paramDto.getOrderMsg().getServiceTime(), "#0000FF")
                        .add(KEYWORD4, paramDto.getOrderMsg().getAddress(), "#0000FF")
                        .add(REMARK, "您可关注微信公众号\"悦管家Life\"或致电400-0022-699跟进订单详情。", "#0000FF")
                        .toJsonStr();
                break;
            case REST_CYCLE_ORDER_REMIND:
                data = templateMsg.setTemplate_id(WeChatConfig.REST_CYCLE_ORDER_REMIND)
                        .setTouser(toUser)
                        .add(FIRST, "购买成功", "#0000FF")
                        .add(KEYWORD1, "杨晓龙", "#0000FF")
                        .add(KEYWORD2, "日常清洁", "#0000FF")
                        .add(KEYWORD3, "100.0", "#0000FF")
                        .add(KEYWORD4, DateUtil.dateToStr(new Date()), "#0000FF")
                        .toJsonStr();
                break;
            case CHANGE_SERVICE:
                data = templateMsg.setTemplate_id(WeChatConfig.CHANGE_SERVICE)
                        .setTouser(toUser)
                        .add(FIRST, "用户取消订单通知", "#0000FF")
                        .add(KEYWORD1, "订单编号（123456）", "#0000FF")
                        .add(KEYWORD2, "日常清洁", "#0000FF")
                        .add(KEYWORD3, "杨晓龙", "#0000FF")
                        .add(KEYWORD4, "100.00", "#0000FF")
                        .add(KEYWORD5, DateUtil.dateToStr(new Date()), "#0000FF")
                        .toJsonStr();
                break;
            case CANCEL_CYCLE_SINGLE_ORDER:
                data = templateMsg.setTemplate_id(WeChatConfig.CANCEL_CYCLE_SINGLE_ORDER)
                        .setTouser(toUser)
                        .add(FIRST, "购买成功", "#0000FF")
                        .add(KEYWORD1, "杨晓龙", "#0000FF")
                        .add(KEYWORD2, "日常清洁", "#0000FF")
                        .add(KEYWORD3, "100.0", "#0000FF")
                        .add(KEYWORD4, DateUtil.dateToStr(new Date()), "#0000FF")
                        .toJsonStr();
                break;
            case CANCEL_ORDER:
                data = templateMsg.setTemplate_id(WeChatConfig.DISPATCH_ORDER_SUCCESS_CUSTOMER)
                        .setTouser(toUser)
                        .add(FIRST, "您的订单已取消！", "#0000FF")
                        .add(KEYWORD1, "（订单号）12345678", "#0000FF")
                        .add(KEYWORD2, "日常清洁", "#0000FF")
                        .add(KEYWORD3, "80.00", "#0000FF")
                        .add(REMARK, "如有疑问，请联系客服", "#0000FF")
                        .toJsonStr();
                break;
            case CANCEL_ORDER_NOTIFY_CUSTOM_SERVICE:
                data = templateMsg.setTemplate_id(WeChatConfig.CHANGE_SERVICE)
                        .setTouser(toUser)
                        .add(FIRST, "订单取消通知", "#0000FF")
                        .add(KEYWORD1, "订单编号（123456）", "#0000FF")
                        .add(KEYWORD2, "日常清洁", "#0000FF")
                        .add(KEYWORD3, "杨晓龙", "#0000FF")
                        .add(KEYWORD4, "100.00", "#0000FF")
                        .add(KEYWORD5, DateUtil.dateToStr(new Date()), "#0000FF")
                        .toJsonStr();
                break;
            case CANCEL_ORDER_NOTIFY_SERVICE:
                data = templateMsg.setTemplate_id(WeChatConfig.CANCEL_ORDER_NOTIFY_SERVICE)
                        .setTouser(toUser)
                        .add(FIRST, "订单取消通知", "#0000FF")
                        .add(KEYWORD1, paramDto.getOrderMsg().getServiceUserName(), "#0000FF")
                        .add(KEYWORD2, paramDto.getOrderMsg().getServiceTime(), "#0000FF")
                        .add(KEYWORD3, paramDto.getOrderMsg().getOrderNo(), "#0000FF")
                        .add(REMARK, "为了不影响您的工作，请重新确认已接订单信息，有任何问题请联系悦管家客服400-0022-699", "#0000FF")
                        .toJsonStr();
                break;
        }
        return data;

    }
}
