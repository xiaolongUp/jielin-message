CREATE TABLE `jl_msg_push` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '名称',
  `operate_type` tinyint(4) NOT NULL COMMENT '操作类型：详见代码中 OperateTypeEnum',
  `option_value` tinyint(4) NOT NULL COMMENT '需要哪些推送：详见代码中 PushTypeEnum',
  `priority` tinyint(4) NOT NULL DEFAULT '1' COMMENT '优先级，数越大优先级越高',
  `enable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `operate_type` (`operate_type`,`option_value`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

--app推送的标题和模版内容
CREATE TABLE `jl_app_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `operate_type` tinyint(4) NOT NULL COMMENT '操作类型：详见代码中 OperateTypeEnum',
  `title` varchar(50) NOT NULL COMMENT 'app推送模版标题',
  `content` varchar(255) NOT NULL COMMENT 'app推送的内容',
  PRIMARY KEY (`id`)
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
  `operate_type` tinyint(4) NOT NULL COMMENT '操作类型code',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
  `enable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

--所有的推送的方式
CREATE TABLE `jl_msg_option` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `push_name` varchar(20) NOT NULL COMMENT '推送类型的名称',
  `push_type` tinyint(4) NOT NULL COMMENT '推送类型的code',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
  `enable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
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
	`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
  `enable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;