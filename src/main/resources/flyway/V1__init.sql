CREATE TABLE `jl_msg_push` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dsc` varchar(50) NOT NULL COMMENT '名称描述',
  `operate_type` tinyint(4) NOT NULL COMMENT '操作类型：jl_msg_operate主键',
  `option_value` tinyint(4) NOT NULL COMMENT '需要哪些推送：jl_msg_option主键',
  `platform` tinyint(4) NOT NULL COMMENT '平台，悦管家平台，jl_msg_platform主键',
  `priority` tinyint(4) NOT NULL DEFAULT '1' COMMENT '优先级，数越大优先级越高',
  `enable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `operate_type` (`operate_type`,`option_value`,`platform`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

--个推cid与别名绑定
CREATE TABLE `jl_gt_alias` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cid` varchar(50) NOT NULL COMMENT '个推客户端的cid',
  `alias` varchar(50) NOT NULL COMMENT '别名',
  `app_type` varchar(20) NOT NULL COMMENT 'app类型',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
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

--插入的推送类型的数据，push_type为固定值，对应数据的枚举，如果修改同时修改枚举的数据
INSERT INTO `jl_msg_option`(`id`, `push_name`, `push_type`, `enable`) VALUES (1, '短信推送', 1, 1);
INSERT INTO `jl_msg_option`(`id`, `push_name`, `push_type`, `enable`) VALUES (2, '微信公众号推送', 2, 1);
INSERT INTO `jl_msg_option`(`id`, `push_name`, `push_type`, `enable`) VALUES (3, 'app推送', 3, 1);
INSERT INTO `jl_msg_option`(`id`, `push_name`, `push_type`, `enable`) VALUES (4, '微信小程序推送', 4, 1);