/*
Navicat MySQL Data Transfer

Source Server         : reins@127.0.0.1_本机
Source Server Version : 50719
Source Host           : localhost:3306
Source Database       : reinsdb

Target Server Type    : MYSQL
Target Server Version : 50719
File Encoding         : 65001

Date: 2019-04-22 10:09:59
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for user_rate
-- ----------------------------
DROP TABLE IF EXISTS `user_rate`;
CREATE TABLE `user_rate` (
  `id` int(15) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `rate` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_rate
-- ----------------------------
INSERT INTO `user_rate` VALUES ('1', '赵志刚', '1.70');
INSERT INTO `user_rate` VALUES ('2', '卢玥', '1.20');
INSERT INTO `user_rate` VALUES ('3', '常若男', '0.60');
INSERT INTO `user_rate` VALUES ('4', '闫明', '0.85');
INSERT INTO `user_rate` VALUES ('5', '王艳艳', '0.75');
