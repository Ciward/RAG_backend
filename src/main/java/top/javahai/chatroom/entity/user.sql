/*
 Navicat Premium Dump SQL

 Source Server         : 学院1
 Source Server Type    : MySQL
 Source Server Version : 80039 (8.0.39-0ubuntu0.20.04.1)
 Source Host           : 10.102.33.6:3306
 Source Schema         : chatroom

 Target Server Type    : MySQL
 Target Server Version : 80039 (8.0.39-0ubuntu0.20.04.1)
 File Encoding         : 65001

 Date: 30/10/2024 20:09:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(20) NOT NULL COMMENT '登录账号',
  `nickname` varchar(20) NOT NULL COMMENT '昵称',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `user_profile` varchar(255) DEFAULT NULL COMMENT '用户头像',
  `user_state_id` int DEFAULT '2' COMMENT '用户状态id',
  `is_enabled` tinyint(1) DEFAULT '1' COMMENT '是否可用',
  `is_locked` tinyint(1) DEFAULT '0' COMMENT '是否被锁定',
  `gender` enum('male','female','other') CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '性别',
  `name` varchar(255) DEFAULT NULL COMMENT '姓名',
  `student_id` varchar(255) DEFAULT NULL COMMENT '学号',
  `nation` varchar(255) DEFAULT NULL,
  `hometown` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_ibfk_1` (`user_state_id`),
  CONSTRAINT `user_ibfk_1` FOREIGN KEY (`user_state_id`) REFERENCES `user_state` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb3;

SET FOREIGN_KEY_CHECKS = 1;
