SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS `o_gateway`;

-- ----------------------------
-- Table structure for route_definition_entity
-- ----------------------------
DROP TABLE IF EXISTS `route_definition_entity`;
CREATE TABLE `route_definition_entity` (
                                           `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '路由唯一标识',
                                           `content` json NOT NULL COMMENT '路由体',
                                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

SET FOREIGN_KEY_CHECKS = 1;
