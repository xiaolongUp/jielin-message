package com.jielin.message.dto;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 推送消息发送时需要的参数
 *
 * @author yxl
 */
@Data
@Accessors(chain = true)
public class ParamDto  {

    //需要发送的推送的类型（OperateTypeEnum）
    private Integer operateType;

    //需要推送的数据到哪个手机号
    private String phoneNumber;

    //需要推送的app
    private String appType;

    //需要的参数
    private List<String> params;

    //微信推送需要的参数信息
    private OrderMsg orderMsg;

    public ParamDto(){
        this.orderMsg = new OrderMsg();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public class OrderMsg {

        //订单号
        private String orderNo;

        //产品名称
        private String productName;

        //客户姓名
        private String customName;

        //用户手机号
        private String customPhone;

        //服务时间
        private String serviceTime;

        //服务人员信息
        private String serviceUserName;

        //订单金额
        private Double money;

        //服务人员性别(此处为悦哥，悦姐)
        private String gender;

        //客户地址
        private String address;
    }

}
