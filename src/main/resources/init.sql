CREATE TABLE `jl_msg_push` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '名称',
  `operate_type` tinyint(4) NOT NULL COMMENT '操作类型：详见代码中 OperateTypeEnum',
  `option_value` tinyint(4) NOT NULL COMMENT '需要哪些推送：详见代码中 PushTypeEnum',
  PRIMARY KEY (`id`)
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