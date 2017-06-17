/*
Navicat MySQL Data Transfer

Source Server         : MySQL
Source Server Version : 50711
Source Host           : localhost:3306
Source Database       : board_db_system

Target Server Type    : MYSQL
Target Server Version : 50711
File Encoding         : 65001

Date: 2017-06-17 11:23:18
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `table_meeting`
-- ----------------------------
DROP TABLE IF EXISTS `table_meeting`;
CREATE TABLE `table_meeting` (
  `meeting_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `meeting_name` varchar(64) CHARACTER SET utf8 DEFAULT NULL,
  `partner_number` bigint(20) DEFAULT NULL,
  `organizer_id` bigint(20) DEFAULT NULL,
  `start_time` datetime(6) DEFAULT NULL,
  `end_time` datetime(6) DEFAULT NULL,
  `note_path` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `meeting_room_id` int(20) DEFAULT NULL,
  PRIMARY KEY (`meeting_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for `table_participate`
-- ----------------------------
DROP TABLE IF EXISTS `table_participate`;
CREATE TABLE `table_participate` (
  `part_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `meeting_id` bigint(20) NOT NULL,
  PRIMARY KEY (`part_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for `table_user`
-- ----------------------------
DROP TABLE IF EXISTS `table_user`;
CREATE TABLE `table_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) DEFAULT NULL,
  `password` varchar(128) DEFAULT NULL,
  `phone_number` varchar(11) DEFAULT NULL,
  `authority` int(11) DEFAULT NULL,
  `head_image` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for `table_whiteboard`
-- ----------------------------
DROP TABLE IF EXISTS `table_whiteboard`;
CREATE TABLE `table_whiteboard` (
  `board_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `board_content` varchar(512) CHARACTER SET utf8 NOT NULL,
  `meeting_id` bigint(20) NOT NULL,
  `board_name` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (`board_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
