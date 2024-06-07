create database bili;
/*
 Navicat Premium Data Transfer

 Source Server         : 182.92.81.46_3306
 Source Server Type    : MySQL
 Source Server Version : 80035
 Source Host           : 182.92.81.46:3306
 Source Schema         : bili

 Target Server Type    : MySQL
 Target Server Version : 80035
 File Encoding         : 65001

 Date: 18/05/2024 14:00:17
*/

USE bili;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
-- ----------------------------
-- Table structure for bili_collection
-- ----------------------------
DROP TABLE IF EXISTS `bili_collection`;
CREATE TABLE `bili_collection`  (
  `folderId` bigint NOT NULL,
  `videoId` bigint NOT NULL,
  `collectTime` datetime NULL DEFAULT NULL COMMENT '收藏时间',
  INDEX `folderId_index`(`folderId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bili_collection
-- ----------------------------
INSERT INTO `bili_collection` VALUES (1, 1, '2024-05-16 18:39:51');
INSERT INTO `bili_collection` VALUES (1, 2, '2024-05-15 18:39:55');

-- ----------------------------
-- Table structure for bili_collection_folder
-- ----------------------------
DROP TABLE IF EXISTS `bili_collection_folder`;
CREATE TABLE `bili_collection_folder`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `userId` bigint NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `userId_index`(`userId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bili_collection_folder
-- ----------------------------
INSERT INTO `bili_collection_folder` VALUES (1, '默认收藏夹', 1, '默认的');

-- ----------------------------
-- Table structure for bili_comment
-- ----------------------------
DROP TABLE IF EXISTS `bili_comment`;
CREATE TABLE `bili_comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `isTop` bit(1) NULL DEFAULT b'0' COMMENT '是否是置顶，默认1不置顶',
  `videoId` bigint NOT NULL COMMENT '视频id',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `publishName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `avatarImg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像图片url',
  `avatarLink` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像链接个人主页',
  `publishLevel` int NULL DEFAULT NULL COMMENT '发布人会员等级',
  `publishTime` datetime NOT NULL COMMENT '发布日期',
  `parentId` bigint NULL DEFAULT NULL COMMENT '父评论id',
  `ipAddress` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ip地址',
  `ipRegion` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ip区域',
  `agreeNumber` bigint NULL DEFAULT 0 COMMENT '点赞数',
  `disagreeNumber` bigint NULL DEFAULT 0 COMMENT '不同意数量',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `parentId_index`(`parentId`) USING BTREE,
  INDEX `videoId_index`(`videoId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bili_comment
-- ----------------------------
INSERT INTO `bili_comment` VALUES (1, b'1', 1, 'asdiahsdf', '123456', '/aaa.jpg', 'isdsjdsj', 1, '2024-05-15 14:05:13', NULL, 'qewqreer', '河南', 0, 0);

-- ----------------------------
-- Table structure for bili_dynamic
-- ----------------------------
DROP TABLE IF EXISTS `bili_dynamic`;
CREATE TABLE `bili_dynamic`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `createTime` datetime NOT NULL,
  `publisherId` bigint NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `videoId` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `publish_index`(`publisherId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bili_dynamic
-- ----------------------------

-- ----------------------------
-- Table structure for bili_follow
-- ----------------------------
DROP TABLE IF EXISTS `bili_follow`;
CREATE TABLE `bili_follow`  (
  `userId` bigint NULL DEFAULT NULL,
  `upId` bigint NULL DEFAULT NULL,
  `followTime` datetime NULL DEFAULT NULL,
  INDEX `up_index`(`upId`) USING BTREE,
  INDEX `user_index`(`userId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bili_follow
-- ----------------------------
INSERT INTO `bili_follow` VALUES (1, 2, '2024-05-30 14:02:40');
INSERT INTO `bili_follow` VALUES (1, 3, '2024-05-30 14:02:40');
INSERT INTO `bili_follow` VALUES (1, 4, '2024-05-30 14:02:40');
INSERT INTO `bili_follow` VALUES (1, 5, '2024-05-30 14:02:40');
INSERT INTO `bili_follow` VALUES (1, 6, '2024-05-30 14:02:40');
INSERT INTO `bili_follow` VALUES (1, 7, '2024-05-30 14:02:40');
INSERT INTO `bili_follow` VALUES (2, 1, '2024-05-30 14:02:40');
INSERT INTO `bili_follow` VALUES (2, 3, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (2, 4, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (2, 5, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (2, 6, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (2, 7, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (2, 8, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (2, 9, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (3, 10, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (3, 1, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (3, 2, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (3, 4, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (3, 5, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (4, 6, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (4, 7, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (4, 8, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (4, 9, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (4, 10, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (4, 11, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (4, 12, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (4, 1, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (4, 2, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (4, 3, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (8, 1, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (8, 2, '2024-05-30 14:02:40');
INSERT INTO `bili_follow` VALUES (8, 3, '2024-05-30 14:02:40');
INSERT INTO `bili_follow` VALUES (8, 4, '2024-05-30 14:02:40');
INSERT INTO `bili_follow` VALUES (8, 5, '2024-05-30 14:02:40');
INSERT INTO `bili_follow` VALUES (5, 6, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (5, 7, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (5, 8, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (5, 9, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (5, 10, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (5, 11, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (5, 12, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (7, 1, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (6, 2, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (6, 3, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (6, 4, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (6, 5, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (6, 9, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (6, 12, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (10, 11, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (10, 12, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (11, 9, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (12, 8, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (11, 7, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (12, 6, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (11, 5, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (12, 4, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (11, 3, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (12, 2, '2024-05-15 14:12:26');
INSERT INTO `bili_follow` VALUES (9, 1, '2024-05-15 14:12:26');

-- ----------------------------
-- Table structure for bili_history
-- ----------------------------
DROP TABLE IF EXISTS `bili_history`;
CREATE TABLE `bili_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `recordTime` datetime NOT NULL COMMENT '记录时间',
  `uid` bigint NULL DEFAULT NULL COMMENT '记录此历史的用户的id',
  `watched` bigint NULL DEFAULT NULL COMMENT '已看完的时长，单位为妙，看完的记录为-1',
  `videoType` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '视频类型',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '视频url',
  `coverUrl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '视频封面图片url',
  `authorAvatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发布者头像url',
  `authorName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发布者姓名',
  `authorLink` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发布者主页后端记录的，没法查询为空，前端再单独设置',
  `isMobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '是否是手机端，也是前端获取',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `uid_index`(`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bili_history
-- ----------------------------

-- ----------------------------
-- Table structure for bili_live_room
-- ----------------------------
DROP TABLE IF EXISTS `bili_live_room`;
CREATE TABLE `bili_live_room`  (
  `roomId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '房间id',
  `startId` bigint NOT NULL COMMENT '发起人id',
  `deleteStatus` bit(1) NULL DEFAULT b'1' COMMENT '是否删除',
  `roomTitle` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '房间标题',
  `roomDesc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '房间简介',
  `notice` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '公告',
  `createTime` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `endTime` datetime NULL DEFAULT NULL COMMENT '直播结束时间',
  `playUrl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '播放url',
  `roomCover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `typeTitle` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`roomId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bili_live_room
-- ----------------------------

-- ----------------------------
-- Table structure for bili_sys_role
-- ----------------------------
DROP TABLE IF EXISTS `bili_sys_role`;
CREATE TABLE `bili_sys_role`  (
  `id` bigint NOT NULL,
  `roleName` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `roleTag` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `deleteStatus` bit(1) NULL DEFAULT b'1',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bili_sys_role
-- ----------------------------
INSERT INTO `bili_sys_role` VALUES (1, 'simple', 'simple', b'1');
INSERT INTO `bili_sys_role` VALUES (2, 'admin', 'admin', b'1');
INSERT INTO `bili_sys_role` VALUES (3, 'vip', 'vip', b'1');

-- ----------------------------
-- Table structure for bili_sys_user
-- ----------------------------
DROP TABLE IF EXISTS `bili_sys_user`;
CREATE TABLE `bili_sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `deleteStatus` bit(1) NULL DEFAULT b'1' COMMENT '是否删除',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码',
  `sex` bit(1) NULL DEFAULT b'1' COMMENT '性别',
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像url',
  `level` int NULL DEFAULT NULL COMMENT '用户等级',
  `coin` bigint NULL DEFAULT NULL COMMENT '金币数量',
  PRIMARY KEY (`id`, `username`) USING BTREE,
  UNIQUE INDEX `uname`(`username`) USING BTREE COMMENT '提高查询速度',
  INDEX `moblie`(`mobile`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bili_sys_user
-- ----------------------------
INSERT INTO `bili_sys_user` VALUES (1, '123456', b'1', 'aaaa', '$2a$06$DYIIuXunLXyXliWvbxIPU.tXSEKJ/36NUCvazc1/r6wN0Ns0yaqVC', b'1', '15939654984', '/a/b', NULL, 100);
INSERT INTO `bili_sys_user` VALUES (2, '234567', b'1', '234567', '$2a$06$yxLeV242yFew/6K6EMZLaew9Mg.E9dLp0w0u2ChwpJ.yvaoNlgUaG', b'1', '15836735249', '/ba/c.png', 1, 100);
INSERT INTO `bili_sys_user` VALUES (3, '010101', b'1', '010101', '$2a$06$3PVS8ZBd7JYoN66r4uy1DeGiHDDQAISdcvVjsAa0d4cebGradApL2', b'1', '15836705249', '/ba/c.png', 1, 100);
INSERT INTO `bili_sys_user` VALUES (4, '121212', b'1', '121212', '$2a$06$K6BrAlVo3BrhvgNzvaR4lOsuUFvD8FSdFq3OlheKokO9NtMYHHoKK', b'1', '15836715249', '/ba/c.png', 1, 200);
INSERT INTO `bili_sys_user` VALUES (5, '232323', b'1', '232323', '$2a$06$X1HfNDcRm4YZVfggTdYFc..kuQ46KH89Adt0y37AUlhbjS56MyZie', b'1', '15836725249', '/ba/c.png', 1, 200);
INSERT INTO `bili_sys_user` VALUES (6, '343434', b'1', '343434', '$2a$06$dqh8/5bE2B0eHFhmVMnfAuQbOV6hZ8Xg65qh3pht9iXm/N18CkyxG', b'1', '15836735249', '/ba/c.png', 1, 200);
INSERT INTO `bili_sys_user` VALUES (7, '454545', b'1', '454545', '$2a$06$4SfBQPRy0x5bp2H8uSqN5eAB7eh7tj6KN.3tEbmJ9DY43ghA7poYq', b'1', '15836745249', '/ba/c.png', 1, 100);
INSERT INTO `bili_sys_user` VALUES (8, '565656', b'1', '565656', '$2a$06$yx3wFl6yOqa.CSwjXL.o2uwM9m2vzHimS8RjSCJQPi7VFySKSi/tq', b'1', '15836755249', '/ba/c.png', 1, 200);
INSERT INTO `bili_sys_user` VALUES (9, '676767', b'1', '676767', '$2a$06$T8btpUKVjBr4p2QwsX5BY.TmpccFwwSax/3Xl3XE/eQ27AN5QlEPC', b'1', '15836765249', '/ba/c.png', 1, 100);
INSERT INTO `bili_sys_user` VALUES (10, '787878', b'1', '787878', '$2a$06$Le5MoB9dVCp2d1jJ2vzgz.b952PtKpLXCXK0wezSm8Kj9RiqXh9PK', b'1', '15836775249', '/ba/c.png', 1, 200);
INSERT INTO `bili_sys_user` VALUES (11, '898989', b'1', '898989', '$2a$06$gkY9GmeXVh.fSulZxrouzOzwGKKyyCbDTom2.7J9TkKO6buLIc0/e', b'1', '15836785249', '/ba/c.png', 1, 100);
INSERT INTO `bili_sys_user` VALUES (12, '910910910', b'1', '910910910', '$2a$06$kiFFosHMGw0ZY6g8W6jOF.m9rZduNQKvvC/Fj0HD.fNA97Dn/vpY6', b'1', '15836795249', '/ba/c.png', 1, 200);

-- ----------------------------
-- Table structure for bili_sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `bili_sys_user_role`;
CREATE TABLE `bili_sys_user_role`  (
  `role_id` bigint NULL DEFAULT NULL,
  `user_id` bigint NULL DEFAULT NULL,
  INDEX `uid`(`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bili_sys_user_role
-- ----------------------------
INSERT INTO `bili_sys_user_role` VALUES (1, 1);

-- ----------------------------
-- Table structure for bili_video
-- ----------------------------
DROP TABLE IF EXISTS `bili_video`;
CREATE TABLE `bili_video`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `deleteStatus` bit(1) NULL DEFAULT b'1' COMMENT '是否删除',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频网址',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  `playTimes` bigint NULL DEFAULT NULL COMMENT '完播数量',
  `groupId` bigint NULL DEFAULT NULL COMMENT '视频分组id要加索引',
  `keyWord` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关键字；未来可以添加到es中方便搜索',
  `typeName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类，不为空，要加索引',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '视频描述',
  `length` bigint NULL DEFAULT NULL COMMENT '视频时长，单位ms',
  `cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '视频封面',
  `transmit` bigint NULL DEFAULT NULL COMMENT '转发数',
  `coin` bigint NULL DEFAULT NULL COMMENT '投币数',
  `collect` bigint NULL DEFAULT NULL COMMENT '收藏数',
  `likeNumber` bigint NULL DEFAULT NULL COMMENT '点赞数',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `groupId_index`(`groupId`) USING BTREE,
  INDEX `typeName_index`(`typeName`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bili_video
-- ----------------------------
INSERT INTO `bili_video` VALUES (1, 'dojf', b'1', 'asdsd', '2024-05-10 22:04:27', 20, 1, 'sds啊四大行，阿叔', 'game', 'dasd1', 1000, NULL, 0, 0, 0, 0);
INSERT INTO `bili_video` VALUES (2, '测试', b'1', '啊孙杜', '2024-04-12 22:07:07', 0, NULL, '阿斯顿撒', 'game', 'dasd1', 2323, NULL, 0, 0, 0, 0);

-- ----------------------------
-- Table structure for bili_video_type
-- ----------------------------
DROP TABLE IF EXISTS `bili_video_type`;
CREATE TABLE `bili_video_type`  (
  `id` bigint NOT NULL,
  `typeName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `isActive` bit(1) NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `tyneName`(`typeName`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bili_video_type
-- ----------------------------

-- ----------------------------
-- Table structure for bili_watcher
-- ----------------------------
DROP TABLE IF EXISTS `bili_watcher`;
CREATE TABLE `bili_watcher`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `stream` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `userId` bigint NULL DEFAULT NULL,
  `service_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `enterTime` datetime NULL DEFAULT NULL,
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `leaveTime` datetime NULL DEFAULT NULL,
  `client_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `stream`(`stream`) USING BTREE,
  INDEX `userId`(`userId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bili_watcher
-- ----------------------------
INSERT INTO `bili_watcher` VALUES (1, 'game-7080601151a24426a24cb869c7b493cc', 1, 'l7h1oi30', '2024-05-13 09:50:37', '172.17.0.1', NULL, NULL);
INSERT INTO `bili_watcher` VALUES (2, 'game-3c564f9420984d05a24c7deca03c684a', 1, 'l7h1oi30', '2024-05-13 09:51:05', '172.17.0.1', NULL, NULL);
INSERT INTO `bili_watcher` VALUES (3, 'game-1107f1fd3035498d8e1b85b13f143592', 1, 'l7h1oi30', '2024-05-13 09:56:08', '172.17.0.1', NULL, NULL);
INSERT INTO `bili_watcher` VALUES (4, 'game-5815834a54fd46f890bb565ec0242db8', 1, 'l7h1oi30', '2024-05-13 09:59:01', '172.17.0.1', NULL, NULL);
INSERT INTO `bili_watcher` VALUES (5, 'game-030986cfef1d44ebb2c333b624d4360f', 1, 'l7h1oi30', '2024-05-13 10:00:02', '172.17.0.1', NULL, NULL);
INSERT INTO `bili_watcher` VALUES (6, 'game-532cffc699cb4db8a2a8c5b0b263f36b', 1, 'l7h1oi30', '2024-05-13 10:01:04', '172.17.0.1', NULL, NULL);
INSERT INTO `bili_watcher` VALUES (7, 'game-7bbeb2742ddc43bcb0ddd5458b0db661', 1, 'l7h1oi30', '2024-05-13 10:04:29', '172.17.0.1', NULL, NULL);
INSERT INTO `bili_watcher` VALUES (8, 'game-2417fbfcc13c43f4a0941dd99b2f75cc', 1, 'l7h1oi30', '2024-05-13 10:07:23', '172.17.0.1', NULL, NULL);
INSERT INTO `bili_watcher` VALUES (9, 'game-50880da0bcdb4e6999f4059bb7b8ac5f', 1, 'l7h1oi30', '2024-05-13 10:09:29', '172.17.0.1', NULL, NULL);
INSERT INTO `bili_watcher` VALUES (10, 'game-51802bc2719848f89c2c7b196810036f', 1, 'l7h1oi30', '2024-05-13 10:14:26', '172.17.0.1', NULL, NULL);

-- ----------------------------
-- Table structure for video_publish
-- ----------------------------
DROP TABLE IF EXISTS `video_publish`;
CREATE TABLE `video_publish`  (
  `publisherId` int NULL DEFAULT NULL,
  `videoId` int NULL DEFAULT NULL,
  INDEX `videoId`(`videoId`) USING BTREE,
  INDEX `publisherId`(`publisherId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of video_publish
-- ----------------------------
INSERT INTO `video_publish` VALUES (1, 1);

SET FOREIGN_KEY_CHECKS = 1;
