-- 短视频内容平台 数据库初始化脚本
-- MySQL 8.0+

CREATE DATABASE IF NOT EXISTS `video_platform` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `video_platform`;

-- ============ 用户表 ============
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(100) NOT NULL COMMENT 'BCrypt加密密码',
    `nickname`    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    `avatar`      VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `signature`   VARCHAR(255) DEFAULT NULL COMMENT '个性签名',
    `gender`      TINYINT      DEFAULT 0 COMMENT '0未知 1男 2女',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '0禁用 1正常',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============ 视频表 ============
DROP TABLE IF EXISTS `video`;
CREATE TABLE `video` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`      BIGINT       NOT NULL COMMENT '发布者',
    `title`        VARCHAR(255) NOT NULL DEFAULT '' COMMENT '标题',
    `content`      TEXT         COMMENT '描述',
    `cover_url`    VARCHAR(255) DEFAULT NULL COMMENT '封面URL',
    `video_url`    VARCHAR(255) NOT NULL COMMENT '视频URL',
    `type`         TINYINT      NOT NULL DEFAULT 1 COMMENT '1视频 2图文',
    `tags`         VARCHAR(255) DEFAULT NULL COMMENT '标签,逗号分隔',
    `duration`     INT          DEFAULT 0 COMMENT '时长(秒)',
    `view_count`   INT          NOT NULL DEFAULT 0,
    `like_count`   INT          NOT NULL DEFAULT 0,
    `comment_count`INT          NOT NULL DEFAULT 0,
    `favorite_count` INT        NOT NULL DEFAULT 0,
    `status`       TINYINT      NOT NULL DEFAULT 1 COMMENT '0下架 1正常 2审核中',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_type_status` (`type`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频/图文表';

-- ============ 图文配图表 ============
DROP TABLE IF EXISTS `image_post`;
CREATE TABLE `image_post` (
    `id`        BIGINT NOT NULL AUTO_INCREMENT,
    `video_id`  BIGINT NOT NULL COMMENT '关联video.id(type=2)',
    `image_url` VARCHAR(255) NOT NULL,
    `sort`      INT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_video_id` (`video_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图文配图';

-- ============ 评论表 ============
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
    `id`         BIGINT   NOT NULL AUTO_INCREMENT,
    `video_id`   BIGINT   NOT NULL,
    `user_id`    BIGINT   NOT NULL,
    `parent_id`  BIGINT   DEFAULT 0 COMMENT '父评论id, 0为顶级',
    `content`    VARCHAR(1000) NOT NULL,
    `like_count` INT      NOT NULL DEFAULT 0,
    `status`     TINYINT  NOT NULL DEFAULT 1,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_video_id` (`video_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- ============ 点赞表 (持久层; 计数用Redis) ============
DROP TABLE IF EXISTS `video_like`;
CREATE TABLE `video_like` (
    `id`         BIGINT NOT NULL AUTO_INCREMENT,
    `user_id`    BIGINT NOT NULL,
    `video_id`   BIGINT NOT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_video` (`user_id`, `video_id`),
    KEY `idx_video_id` (`video_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞记录';

-- ============ 收藏表 ============
DROP TABLE IF EXISTS `video_favorite`;
CREATE TABLE `video_favorite` (
    `id`         BIGINT NOT NULL AUTO_INCREMENT,
    `user_id`    BIGINT NOT NULL,
    `video_id`   BIGINT NOT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_video` (`user_id`, `video_id`),
    KEY `idx_video_id` (`video_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏记录';

-- ============ 关注表 ============
DROP TABLE IF EXISTS `user_follow`;
CREATE TABLE `user_follow` (
    `id`         BIGINT NOT NULL AUTO_INCREMENT,
    `user_id`    BIGINT NOT NULL COMMENT '粉丝',
    `follow_id`  BIGINT NOT NULL COMMENT '被关注者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_follow` (`user_id`, `follow_id`),
    KEY `idx_follow_id` (`follow_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='关注关系';

-- ============ 文件分片上传记录 ============
DROP TABLE IF EXISTS `upload_chunk`;
CREATE TABLE `upload_chunk` (
    `id`          BIGINT NOT NULL AUTO_INCREMENT,
    `upload_id`   VARCHAR(64) NOT NULL COMMENT '上传任务ID',
    `chunk_index` INT NOT NULL,
    `chunk_size`  BIGINT NOT NULL,
    `file_hash`   VARCHAR(64) DEFAULT NULL,
    `user_id`     BIGINT NOT NULL,
    `complete`    TINYINT NOT NULL DEFAULT 0,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_upload_chunk` (`upload_id`, `chunk_index`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分片上传记录';

-- ============ 初始数据 ============
INSERT INTO `user` (`username`, `password`, `nickname`, `avatar`, `signature`)
VALUES ('admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '超级管理员',
        'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', '官方账号');

INSERT INTO `user` (`username`, `password`, `nickname`, `avatar`)
VALUES ('demo', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '体验用户',
        'https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png');
-- 上述密码hash对应明文: 123456
