# 操作文档

## 准备工作
### 1.需要初始化mysql数据库（init.sql）
* jl_msg_operate（操作类型表，例如：下单成功通知服务人员）
* jl_msg_option（推送类型表，例如：app推送，短线推送）
* jl_msg_platform（消息所属平台表）
* jl_msg_push（操作推送类型表，具体某个操作的某种推送方式）
* jl_msg_uniapp（uniapp数据库配置信息）
### 2.mongodb数据库（mongo_init.txt）
* jl_msg_template（各种推送方式的模版数据）

## 消息队列和推送方式说明
**整个消息系统采用的是异步消费的形式推送所有的需要推送的信息，也可使用同步接口去推送数据**
此次使用的消息队列为rabbitmq，下单操作的推送队列名称为：**push_msg**，
推送方式有以下几种，更多的推送方式，请继承**MsgPush**抽象类

- 微信公众号
- 微信小程序
- 短线推送
+ 手机app（极光推送，个推推送，其他的推送方式请实现**AppMsgPushHandler**接口）
    * 离线推送
    * 在线推送
    

部门之间的推送数据采用的是钉钉推送，消息队列的名称为：**ding_push_msg**

## 流程说明
需要推送消息的系统需要接入rabbitmq的生产者，生产消息

```flow
product=>start: 消息生产者
mq=>operation: rabbitmq
costomer=>operation: 消息推送系统
cond=>condition: 是否需要推送数据 Yes or No?
handler=>operation: 各种推送的实现
e=>end: 推送结束

product->mq->costomer->cond
cond(yes)->handler
cond(no)->costomer
handler->e
```