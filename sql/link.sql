/*
 Navicat Premium Dump SQL

 Source Server         : 116.198.205.100
 Source Server Type    : MySQL
 Source Server Version : 50726 (5.7.26-log)
 Source Host           : 116.198.205.100:9992
 Source Schema         : link

 Target Server Type    : MySQL
 Target Server Version : 50726 (5.7.26-log)
 File Encoding         : 65001

 Date: 29/12/2024 18:09:47
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_group
-- ----------------------------
DROP TABLE IF EXISTS `t_group`;
CREATE TABLE `t_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `gid` varchar(32) DEFAULT NULL COMMENT '分组标识',
  `name` varchar(64) DEFAULT NULL COMMENT '分组名称',
  `username` varchar(256) DEFAULT NULL COMMENT '创建分组用户名',
  `sort_order` int(11) DEFAULT NULL COMMENT '分组排序',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_unique_username_gid` (`gid`,`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1871862880867192835 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for t_link
-- ----------------------------
DROP TABLE IF EXISTS `t_link`;
CREATE TABLE `t_link` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `domain` varchar(128) DEFAULT NULL COMMENT '域名',
  `short_uri` varchar(8) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '短链接',
  `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
  `origin_url` varchar(1024) DEFAULT NULL COMMENT '原始链接',
  `click_num` int(11) DEFAULT '0' COMMENT '点击量',
  `gid` varchar(32) DEFAULT 'default' COMMENT '分组标识',
  `username` varchar(256) NOT NULL COMMENT '用户名',
  `favicon` varchar(256) DEFAULT NULL COMMENT '网站图标',
  `enable_status` tinyint(1) DEFAULT NULL COMMENT '启用标识 0：启用 1：未启用',
  `created_type` tinyint(1) DEFAULT NULL COMMENT '创建类型 0：接口创建 1：控制台创建',
  `valid_date_type` tinyint(1) DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：自定义',
  `valid_date` datetime DEFAULT NULL COMMENT '有效期',
  `describe` varchar(1024) DEFAULT NULL COMMENT '描述',
  `total_pv` int(11) DEFAULT NULL COMMENT '历史PV',
  `total_uv` int(11) DEFAULT NULL COMMENT '历史UV',
  `total_uip` int(11) DEFAULT NULL COMMENT '历史UIP',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_unique_short-uri` (`short_uri`) USING BTREE,
  KEY `idx_username_gid` (`username`,`gid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1871863237110403074 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for t_link_access_logs
-- ----------------------------
DROP TABLE IF EXISTS `t_link_access_logs`;
CREATE TABLE `t_link_access_logs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
  `user` varchar(64) DEFAULT NULL COMMENT '用户信息',
  `ip` varchar(64) DEFAULT NULL COMMENT 'IP',
  `browser` varchar(64) DEFAULT NULL COMMENT '浏览器',
  `os` varchar(64) DEFAULT NULL COMMENT '操作系统',
  `network` varchar(64) DEFAULT NULL COMMENT '访问网络',
  `device` varchar(64) DEFAULT NULL COMMENT '访问设备',
  `locale` varchar(256) DEFAULT NULL COMMENT '地区',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_full_short_url` (`full_short_url`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1872971014654464002 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for t_link_access_stats
-- ----------------------------
DROP TABLE IF EXISTS `t_link_access_stats`;
CREATE TABLE `t_link_access_stats` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
  `date` date DEFAULT NULL COMMENT '日期',
  `pv` int(11) DEFAULT NULL COMMENT '访问量',
  `uv` int(11) DEFAULT NULL COMMENT '独立访客数',
  `uip` int(11) DEFAULT NULL COMMENT '独立IP数',
  `hour` int(3) DEFAULT NULL COMMENT '小时',
  `weekday` int(3) DEFAULT NULL COMMENT '星期',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_unique_access_stats` (`full_short_url`,`date`,`hour`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8538 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for t_link_browser_stats
-- ----------------------------
DROP TABLE IF EXISTS `t_link_browser_stats`;
CREATE TABLE `t_link_browser_stats` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
  `date` date DEFAULT NULL COMMENT '日期',
  `cnt` int(11) DEFAULT NULL COMMENT '访问量',
  `browser` varchar(64) DEFAULT NULL COMMENT '浏览器',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_unique_browser_stats` (`full_short_url`,`date`,`browser`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8538 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for t_link_device_stats
-- ----------------------------
DROP TABLE IF EXISTS `t_link_device_stats`;
CREATE TABLE `t_link_device_stats` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
  `date` date DEFAULT NULL COMMENT '日期',
  `cnt` int(11) DEFAULT NULL COMMENT '访问量',
  `device` varchar(64) DEFAULT NULL COMMENT '访问设备',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_unique_browser_stats` (`full_short_url`,`date`,`device`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8538 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for t_link_locale_stats
-- ----------------------------
DROP TABLE IF EXISTS `t_link_locale_stats`;
CREATE TABLE `t_link_locale_stats` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
  `date` date DEFAULT NULL COMMENT '日期',
  `cnt` int(11) DEFAULT NULL COMMENT '访问量',
  `province` varchar(64) DEFAULT NULL COMMENT '省份名称',
  `city` varchar(64) DEFAULT NULL COMMENT '市名称',
  `adcode` varchar(64) DEFAULT NULL COMMENT '城市编码',
  `country` varchar(64) DEFAULT NULL COMMENT '国家标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_unique_locale_stats` (`full_short_url`,`date`,`adcode`,`province`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7244 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for t_link_network_stats
-- ----------------------------
DROP TABLE IF EXISTS `t_link_network_stats`;
CREATE TABLE `t_link_network_stats` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
  `date` date DEFAULT NULL COMMENT '日期',
  `cnt` int(11) DEFAULT NULL COMMENT '访问量',
  `network` varchar(64) DEFAULT NULL COMMENT '访问网络',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_unique_browser_stats` (`full_short_url`,`date`,`network`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8538 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for t_link_os_stats
-- ----------------------------
DROP TABLE IF EXISTS `t_link_os_stats`;
CREATE TABLE `t_link_os_stats` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
  `date` date DEFAULT NULL COMMENT '日期',
  `cnt` int(11) DEFAULT NULL COMMENT '访问量',
  `os` varchar(64) DEFAULT NULL COMMENT '操作系统',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_unique_os_stats` (`full_short_url`,`date`,`os`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8538 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for t_link_stats_today
-- ----------------------------
DROP TABLE IF EXISTS `t_link_stats_today`;
CREATE TABLE `t_link_stats_today` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `full_short_url` varchar(128) DEFAULT NULL COMMENT '短链接',
  `date` date DEFAULT NULL COMMENT '日期',
  `today_pv` int(11) DEFAULT '0' COMMENT '今日PV',
  `today_uv` int(11) DEFAULT '0' COMMENT '今日UV',
  `today_uip` int(11) DEFAULT '0' COMMENT '今日IP数',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_unique_today_stats` (`full_short_url`,`date`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8538 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(256) DEFAULT NULL COMMENT '用户名',
  `password` varchar(512) DEFAULT NULL COMMENT '密码',
  `real_name` varchar(256) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(128) DEFAULT NULL COMMENT '手机号',
  `mail` varchar(512) DEFAULT NULL COMMENT '邮箱',
  `deletion_time` bigint(20) DEFAULT NULL COMMENT '注销时间戳',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_unique_username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1871861829640392707 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
