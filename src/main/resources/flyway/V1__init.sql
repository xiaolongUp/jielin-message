CREATE TABLE `jl_msg_push` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dsc` varchar(50) NOT NULL COMMENT '名称描述',
  `operate_type` tinyint(4) NOT NULL COMMENT '操作类型：jl_msg_operate主键',
  `option_value` tinyint(4) NOT NULL COMMENT '需要哪些推送：jl_msg_option主键',
  `platform` tinyint(4) NOT NULL COMMENT '平台，悦管家平台，jl_msg_platform主键',
  `user_type` varchar(50) DEFAULT NULL COMMENT '用户类型，provider，customer',
  `priority` tinyint(4) NOT NULL DEFAULT '1' COMMENT '优先级，数越大优先级越高',
  `enable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `next_stop` tinyint(1) NOT NULL DEFAULT '0' COMMENT '当前规则推送成功，接下来的规则不推送',
  PRIMARY KEY (`id`),
  UNIQUE KEY `operate_type` (`operate_type`,`option_value`,`platform`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

--个推cid与别名绑定
CREATE TABLE `jl_user_gt_alias` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT 'jl_msg_user主键',
  `cid` varchar(50) NOT NULL COMMENT '个推客户端的cid',
  `alias` varchar(50) NOT NULL COMMENT '别名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;


--可操作的tab
CREATE TABLE `jl_msg_menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '名称',
  `icon` varchar(50) DEFAULT "" COMMENT '图标',
  `url` varchar(20) DEFAULT NULL COMMENT '访问uri',
  `parent` int(11) DEFAULT NULL COMMENT '父节点',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

--所有推送操作动作类型和描述
CREATE TABLE `jl_msg_operate` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `operate_name` varchar(50) NOT NULL COMMENT '操作名称',
  `operate_type` tinyint(4) NOT NULL COMMENT '操作类型code，所有的系统需要统一',
  `enable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `push_all` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否所有的推送方式都推送',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

--所有的推送的方式
CREATE TABLE `jl_msg_option` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `push_name` varchar(20) NOT NULL COMMENT '推送类型的名称',
  `push_type` tinyint(4) NOT NULL COMMENT '推送类型的code，所有的系统需要统一',
  `enable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

--推送来自哪个平台
CREATE TABLE `jl_msg_platform` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL COMMENT '平台名称',
  `platform_code` tinyint(4) NOT NULL COMMENT '平台编码，所有的系统需要统一',
  `enable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

--uniapp的配置类，获取所有的app的config
CREATE TABLE `jl_msg_uniapp` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `platform` tinyint(4) NOT NULL COMMENT '平台，悦管家平台，jl_msg_platform主键',
  `app_type` varchar(20) NOT NULL COMMENT 'app类型',
  `app_id` varchar(50) NOT NULL COMMENT 'uniapp的appid',
  `app_secret` varchar(50) NOT NULL COMMENT 'uniapp的app_secret',
  `app_key` varchar(50) NOT NULL COMMENT 'uniapp的app_key',
  `master_secret` varchar(50) NOT NULL COMMENT 'uniapp的master_secret',
  `enable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

--jl_msg_operate
INSERT INTO `jl_msg_operate`(`id`, `operate_name`, `operate_type`, `enable`, `push_all`, `create_time`, `update_time`) VALUES (1, '创建订单推送消息', 101, 1, 0, '2019-11-06 17:47:36', '2019-11-06 17:47:36');
INSERT INTO `jl_msg_operate`(`id`, `operate_name`, `operate_type`, `enable`, `push_all`, `create_time`, `update_time`) VALUES (2, '订单完成', 104, 1, 0, '2019-11-13 09:43:25', '2019-11-13 09:45:55');
INSERT INTO `jl_msg_operate`(`id`, `operate_name`, `operate_type`, `enable`, `push_all`, `create_time`, `update_time`) VALUES (3, '取消订单通知客户', 111, 1, 0, '2019-11-13 09:49:20', '2019-11-13 09:49:33');
INSERT INTO `jl_msg_operate`(`id`, `operate_name`, `operate_type`, `enable`, `push_all`, `create_time`, `update_time`) VALUES (4, '派单成功通知悦姐', 102, 1, 0, '2019-11-21 09:36:16', '2019-11-21 09:36:36');
INSERT INTO `jl_msg_operate`(`id`, `operate_name`, `operate_type`, `enable`, `push_all`, `create_time`, `update_time`) VALUES (5, '取消订单通知悦姐', 113, 1, 0, '2019-11-21 12:56:23', '2019-11-21 12:57:37');

--插入的推送类型的数据，push_type为固定值，对应数据的枚举，如果修改同时修改枚举的数据
INSERT INTO `jl_msg_option`(`id`, `push_name`, `push_type`, `enable`) VALUES (1, '短信推送', 1, 1);
INSERT INTO `jl_msg_option`(`id`, `push_name`, `push_type`, `enable`) VALUES (2, '微信公众号推送', 2, 1);
INSERT INTO `jl_msg_option`(`id`, `push_name`, `push_type`, `enable`) VALUES (3, 'app推送', 3, 1);
INSERT INTO `jl_msg_option`(`id`, `push_name`, `push_type`, `enable`) VALUES (4, '微信小程序推送', 4, 1);
INSERT INTO `jl_msg_option`(`id`, `push_name`, `push_type`, `enable`) VALUES (5, '钉钉推送', 5, 1);

--jl_msg_platform
INSERT INTO `jl_msg_platform`(`id`, `name`, `platform_code`, `enable`, `create_time`, `update_time`) VALUES (1, '悦管家平台', 0, 1, '2019-11-06 17:45:54', '2019-11-06 17:45:54');

--jl_msg_push
INSERT INTO `jl_msg_push`(`id`, `dsc`, `operate_type`, `option_value`, `platform`, `priority`, `enable`, `next_stop`, `user_type`) VALUES (1, '创建订单推送数据', 1, 1, 1, 1, 1, 1, 'provider');
INSERT INTO `jl_msg_push`(`id`, `dsc`, `operate_type`, `option_value`, `platform`, `priority`, `enable`, `next_stop`, `user_type`) VALUES (2, '创建订单推送数据-公众号', 1, 2, 1, 1, 1, 1, 'provider');
INSERT INTO `jl_msg_push`(`id`, `dsc`, `operate_type`, `option_value`, `platform`, `priority`, `enable`, `next_stop`, `user_type`) VALUES (3, '创建订单推送数据-小程序', 1, 3, 1, 1, 1,1, 'provider');
INSERT INTO `jl_msg_push`(`id`, `dsc`, `operate_type`, `option_value`, `platform`, `priority`, `enable`, `next_stop`, `user_type`) VALUES (4, '创建订单小程序推送数据', 1, 4, 1, 1, 1,1, 'provider');
INSERT INTO `jl_msg_push`(`id`, `dsc`, `operate_type`, `option_value`, `platform`, `priority`, `enable`, `next_stop`, `user_type`) VALUES (5, '订单完成小程序推送', 2, 4, 1, 1, 1,1, 'provider');
INSERT INTO `jl_msg_push`(`id`, `dsc`, `operate_type`, `option_value`, `platform`, `priority`, `enable`, `next_stop`, `user_type`) VALUES (6, '取消订单通知客户', 3, 4, 1, 1, 1,1, 'provider');
INSERT INTO `jl_msg_push`(`id`, `dsc`, `operate_type`, `option_value`, `platform`, `priority`, `enable`, `next_stop`, `user_type`) VALUES (7, '排单成功（短信）', 4, 1, 1, 1, 1,1, 'provider');
INSERT INTO `jl_msg_push`(`id`, `dsc`, `operate_type`, `option_value`, `platform`, `priority`, `enable`, `next_stop`, `user_type`) VALUES (8, '排单成功（公众号）', 4, 2, 1, 1, 1,1, 'provider');
INSERT INTO `jl_msg_push`(`id`, `dsc`, `operate_type`, `option_value`, `platform`, `priority`, `enable`, `next_stop`, `user_type`) VALUES (9, '排单成功（app）', 4, 3, 1, 1, 1,1, 'provider');
INSERT INTO `jl_msg_push`(`id`, `dsc`, `operate_type`, `option_value`, `platform`, `priority`, `enable`, `next_stop`, `user_type`) VALUES (11, '排单成功（小程序）', 4, 4, 1, 1, 1,1, 'provider');
INSERT INTO `jl_msg_push`(`id`, `dsc`, `operate_type`, `option_value`, `platform`, `priority`, `enable`, `next_stop`, `user_type`) VALUES (12, '取消订单通知悦姐（短信）', 5, 1, 1, 1, 1,1, 'provider');
INSERT INTO `jl_msg_push`(`id`, `dsc`, `operate_type`, `option_value`, `platform`, `priority`, `enable`, `next_stop`, `user_type`) VALUES (13, '取消订单通知悦姐（公众号）', 5, 2, 1, 1, 1,1, 'provider');
INSERT INTO `jl_msg_push`(`id`, `dsc`, `operate_type`, `option_value`, `platform`, `priority`, `enable`, `next_stop`, `user_type`) VALUES (17, '取消订单通知悦姐（app）', 5, 3, 1, 1, 1,1, 'provider');
INSERT INTO `jl_msg_push`(`id`, `dsc`, `operate_type`, `option_value`, `platform`, `priority`, `enable`, `next_stop`, `user_type`) VALUES (18, '取消订单通知悦姐（小程序）', 5, 4, 1, 1, 1,1, 'provider');

--悦姐uniapp的参数
INSERT INTO `jl_msg_uniapp`(`id`, `app_type`, `app_id`, `app_secret`, `app_key`, `master_secret`, `enable`, `create_time`, `update_time`, `platform`) VALUES (1, 'provider', '2koe01I7T19y6AqSWBLcs4', 'Zxgm8qB0xxAebPfvWWYgS8', 'Ih4SdZhHkX9fJxlPpNmQr6', 'Ugg92J4n6b9xLrrbtshtt4', 1, '2019-11-20 09:51:59', '2019-11-20 09:53:44', 1);

--msg系统保存发送消息的平台的用户信息数据
CREATE TABLE `jl_msg_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `platform` int(4) NOT NULL COMMENT '平台编码，所有的系统需要统一',
  `user_type` varchar(20) NOT NULL COMMENT '所在平台的用户类型',
  `user_id` int(11) NOT NULL COMMENT '所在平台的用户id',
  `user_phone` varchar(20) NOT NULL COMMENT '所在平台用户手机号',
  `uniapp_alias` varchar(50) DEFAULT NULL COMMENT '用户的个推的别名',
  `wx_mp_openid` varchar(50) DEFAULT NULL COMMENT '用户小程序的openid',
  `wx_gzh_openid` varchar(50) DEFAULT NULL COMMENT '用户的微信公众号的openid',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY(platform, user_type,user_id),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--第三方接口调用
CREATE TABLE `jl_msg_third` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `platform` int(4) NOT NULL COMMENT '所属平台',
	`user_type` varchar(20) DEFAULT NULL COMMENT '用户类型',
	`push_type` int(4) DEFAULT NULL COMMENT '推送类型',
  `url` varchar(255) DEFAULT NULL COMMENT '访问uri',
  `http_method` varchar(10) DEFAULT NULL COMMENT '请求类型',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

--投递结果表
CREATE TABLE `jl_msg_send_result` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `correlation_id` varchar(20) NOT NULL COMMENT '消息的uuid',
  `message_id` varchar(255) DEFAULT NULL COMMENT '保证幂等性的唯一标示，暂时没用到',
  `content` text NOT NULL COMMENT '投递的消息内容',
  `result` int(2) NOT NULL COMMENT '0:投递中，1:投递成功，2:投递失败',
  PRIMARY KEY (`id`),
  UNIQUE KEY `correlation_id` (`correlation_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;