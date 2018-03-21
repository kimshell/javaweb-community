/*
Navicat MySQL Data Transfer

Source Server Version : 50717
Source Database       : javaweb_community

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2018-03-05 21:00:32
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for jw_attitude
-- ----------------------------
DROP TABLE IF EXISTS `jw_attitude`;
CREATE TABLE `jw_attitude` (
  `attitude_id` varchar(50) NOT NULL,
  `target_id` varchar(50) DEFAULT NULL COMMENT '目标id',
  `target` varchar(20) DEFAULT NULL COMMENT '目标',
  `type` varchar(20) DEFAULT NULL COMMENT '类型',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `modify_date` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建用户',
  `status` varchar(10) DEFAULT NULL COMMENT '状态',
  `sorted` int(255) DEFAULT NULL COMMENT '排序字段',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`attitude_id`),
  UNIQUE KEY `target_id` (`target_id`,`target`,`type`,`create_user`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='态度';

-- ----------------------------
-- Table structure for jw_collection
-- ----------------------------
DROP TABLE IF EXISTS `jw_collection`;
CREATE TABLE `jw_collection` (
  `collection_id` varchar(50) NOT NULL COMMENT 'pk',
  `post_id` varchar(50) DEFAULT NULL COMMENT '帖子id',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `modify_date` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建用户',
  `status` varchar(10) DEFAULT NULL COMMENT '状态',
  `sorted` int(255) DEFAULT NULL COMMENT '排序字段',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`collection_id`),
  UNIQUE KEY `post_id` (`post_id`,`create_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏';

-- ----------------------------
-- Table structure for jw_friendlink
-- ----------------------------
DROP TABLE IF EXISTS `jw_friendlink`;
CREATE TABLE `jw_friendlink` (
  `frend_link_id` varchar(50) NOT NULL,
  `name` varchar(50) DEFAULT NULL COMMENT '名称',
  `url` varchar(255) DEFAULT NULL COMMENT '连接地址',
  `logo` varchar(255) DEFAULT NULL COMMENT 'logo',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `modify_date` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建用户',
  `status` varchar(10) DEFAULT NULL COMMENT '状态',
  `sorted` int(255) DEFAULT NULL COMMENT '排序字段',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`frend_link_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='友情链接';

-- ----------------------------
-- Table structure for jw_login_record
-- ----------------------------
DROP TABLE IF EXISTS `jw_login_record`;
CREATE TABLE `jw_login_record` (
  `record_id` varchar(50) NOT NULL COMMENT 'pk',
  `user_id` varchar(50) DEFAULT NULL COMMENT '用户id',
  `user_agent` varchar(255) DEFAULT NULL COMMENT '浏览器',
  `ip` varchar(50) DEFAULT NULL COMMENT 'ip',
  `address` varchar(50) DEFAULT NULL COMMENT '地址',
  `operator` varchar(50) DEFAULT NULL COMMENT '运营商',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `modify_date` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建用户',
  `status` varchar(10) DEFAULT NULL COMMENT '状态',
  `sorted` int(255) DEFAULT NULL COMMENT '排序字段',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录记录';

-- ----------------------------
-- Table structure for jw_message
-- ----------------------------
DROP TABLE IF EXISTS `jw_message`;
CREATE TABLE `jw_message` (
  `message_id` varchar(50) NOT NULL COMMENT 'pk',
  `type` varchar(20) DEFAULT NULL COMMENT '类型',
  `post_id` varchar(50) DEFAULT NULL COMMENT '帖子id',
  `reply_id` varchar(50) DEFAULT NULL COMMENT '回复id',
  `is_read` tinyint(1) DEFAULT NULL COMMENT '是否已读',
  `user_id` varchar(50) DEFAULT NULL COMMENT '目标用户id',
  `at_anchor` varchar(50) DEFAULT NULL COMMENT 'at锚点',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `modify_date` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建用户',
  `status` varchar(10) DEFAULT NULL COMMENT '状态',
  `sorted` int(255) DEFAULT NULL COMMENT '排序字段',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息通知';

-- ----------------------------
-- Table structure for jw_post
-- ----------------------------
DROP TABLE IF EXISTS `jw_post`;
CREATE TABLE `jw_post` (
  `post_id` varchar(50) NOT NULL COMMENT 'pk',
  `title` varchar(50) DEFAULT NULL COMMENT '标题',
  `content` longtext COMMENT '正文',
  `anonymous` tinyint(1) DEFAULT NULL COMMENT '是否匿名',
  `type` varchar(20) DEFAULT NULL COMMENT '类型',
  `browse` int(255) DEFAULT NULL COMMENT '浏览量',
  `essence` tinyint(1) DEFAULT NULL COMMENT '是否是精品',
  `top` tinyint(1) DEFAULT NULL COMMENT '是否置顶',
  `reply_control` varchar(20) DEFAULT NULL COMMENT '回复控制',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `modify_date` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建用户',
  `status` varchar(10) DEFAULT NULL COMMENT '状态',
  `sorted` int(255) DEFAULT NULL COMMENT '排序字段',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子';

-- ----------------------------
-- Table structure for jw_post_reply
-- ----------------------------
DROP TABLE IF EXISTS `jw_post_reply`;
CREATE TABLE `jw_post_reply` (
  `reply_id` varchar(50) NOT NULL COMMENT 'pk',
  `post_id` varchar(50) DEFAULT NULL COMMENT '帖子id',
  `content` text COMMENT '回帖正文',
  `anonymous` tinyint(1) DEFAULT NULL COMMENT '是否是匿名回帖',
  `parent_id` varchar(50) DEFAULT NULL COMMENT '父级评论id',
  `is_parent` tinyint(1) DEFAULT NULL COMMENT '是否是父级评论',
  `page` int(255) DEFAULT NULL COMMENT '页码',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `modify_date` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建用户',
  `status` varchar(10) DEFAULT NULL COMMENT '状态',
  `sorted` int(255) DEFAULT NULL COMMENT '排序字段',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`reply_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回帖';

-- ----------------------------
-- Table structure for jw_user
-- ----------------------------
DROP TABLE IF EXISTS `jw_user`;
CREATE TABLE `jw_user` (
  `user_id` varchar(50) NOT NULL COMMENT '用户id',
  `email` varchar(50) NOT NULL COMMENT '邮箱地址',
  `pass` varchar(255) DEFAULT NULL COMMENT '登录密码',
  `phone` varchar(50) DEFAULT NULL COMMENT '手机号码',
  `name` varchar(20) NOT NULL COMMENT '昵称',
  `age` int(10) DEFAULT NULL COMMENT '年龄',
  `gender` varchar(5) DEFAULT NULL COMMENT '性别',
  `portrait` varchar(255) DEFAULT NULL COMMENT '头像',
  `personality` varchar(255) DEFAULT NULL COMMENT '个性签名',
  `site` varchar(255) DEFAULT NULL COMMENT '个人站点',
  `github` varchar(255) DEFAULT NULL COMMENT 'github地址',
  `role` varchar(20) DEFAULT NULL COMMENT '角色',
  `email_verifi` tinyint(1) DEFAULT NULL COMMENT '邮箱是否验证',
  `phone_verifi` tinyint(1) DEFAULT NULL COMMENT '电话号码是否验证',
  `login_radio` tinyint(1) DEFAULT NULL COMMENT '隐私设置-广播登录消息',
  `browse_radio` tinyint(1) DEFAULT NULL COMMENT '隐私设置-广播阅读消息',
  `reply_radio` tinyint(1) DEFAULT NULL COMMENT '隐私设置-广播回复消息',
  `session_id` varchar(50) DEFAULT NULL COMMENT '会话id',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `modify_date` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建用户',
  `status` varchar(10) DEFAULT NULL COMMENT '状态',
  `sorted` int(255) DEFAULT NULL COMMENT '排序字段',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`) USING BTREE,
  UNIQUE KEY `name` (`name`) USING BTREE,
  UNIQUE KEY `phone` (`phone`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户';

-- init admin
INSERT INTO `jw_user` (`user_id`, `email`, `pass`, `phone`, `name`, `age`, `gender`, `portrait`, `personality`, `site`, `github`, `role`, `email_verifi`, `phone_verifi`, `login_radio`, `browse_radio`, `reply_radio`, `session_id`, `create_date`, `modify_date`, `create_user`, `status`, `sorted`, `remark`) VALUES 
('0482ECBDA8B34AF4869ADE1CD6C42154', 'test@test.com', '14e1b600b1fd579f47433b88e8d85291', NULL, 'admin', NULL, NULL, 'http://www.javaweb.io/static/image/default_portrait_03.png', '', '', '', 'ADMIN', '1', '0', '1', '1', '1', NULL, NOW(), NULL, NULL, 'NORMAL', NULL, NULL);


