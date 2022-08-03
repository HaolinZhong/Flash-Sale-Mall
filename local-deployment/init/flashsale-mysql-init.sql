
--
-- Table structure for table `item`
--

DROP TABLE IF EXISTS `item`;

CREATE TABLE `item` (
                        `id` int NOT NULL AUTO_INCREMENT,
                        `title` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
                        `price` double NOT NULL DEFAULT '0',
                        `description` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
                        `sales` int NOT NULL DEFAULT '0',
                        `img_url` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO `item` VALUES (1,'iphone',800,'best apple phone',0,'https://www.logodesignlove.com/wp-content/uploads/2016/09/apple-logo-rob-janoff-01.jpg'),(2,'Iphone 15',1000,'Latest apple phone',0,'https://ss7.vzw.com/is/image/VerizonWireless/apple-iphone-13-pro-max-green-2022?hei=400');


--
-- Table structure for table `item_stock`
--

DROP TABLE IF EXISTS `item_stock`;

CREATE TABLE `item_stock` (
                              `id` int NOT NULL AUTO_INCREMENT,
                              `stock` int NOT NULL DEFAULT '0',
                              `item_id` int NOT NULL DEFAULT '0',
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `item_stock` VALUES (1,100,1),(2,100,2);

--
-- Table structure for table `order_info`
--

DROP TABLE IF EXISTS `order_info`;

CREATE TABLE `order_info` (
                              `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
                              `user_id` int NOT NULL DEFAULT '0',
                              `item_id` int NOT NULL DEFAULT '0',
                              `item_price` double NOT NULL DEFAULT '0',
                              `amount` int NOT NULL DEFAULT '0',
                              `total_price` double NOT NULL DEFAULT '0',
                              `promo_id` int NOT NULL DEFAULT '0',
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--
-- Table structure for table `promo`
--

DROP TABLE IF EXISTS `promo`;

CREATE TABLE `promo` (
                         `id` int NOT NULL AUTO_INCREMENT,
                         `promo_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
                         `start_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         `item_id` int NOT NULL DEFAULT '0',
                         `promo_item_price` double NOT NULL DEFAULT '0',
                         `end_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `promo` VALUES (1,'Black Friday 2022','2022-07-27 23:45:00',2,100,'2022-08-31 03:29:41'),(2,'Old iPhone Sales','2022-07-28 04:06:11',1,88,'2023-07-29 02:25:30');

--
-- Table structure for table `sequence_info`
--

DROP TABLE IF EXISTS `sequence_info`;

CREATE TABLE `sequence_info` (
                                 `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                                 `current_value` int NOT NULL DEFAULT '0',
                                 `step` int NOT NULL DEFAULT '0',
                                 PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO `sequence_info` VALUES ('order_info',0,1);

--
-- Table structure for table `user_info`
--

DROP TABLE IF EXISTS `user_info`;

CREATE TABLE `user_info` (
                             `id` int NOT NULL AUTO_INCREMENT,
                             `name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
                             `gender` tinyint NOT NULL DEFAULT '0' COMMENT 'default 0; 0 for female, 1 for male',
                             `age` int NOT NULL,
                             `tel` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
                             `register_mode` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
                             `third_party_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `user_info_tel_uindex` (`tel`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO `user_info` VALUES (1,'John Doe',1,28,'646000000','byPhone',''),(2,'Jane Doe',0,22,'123000000','byPhone','');


--
-- Table structure for table `user_password`
--

DROP TABLE IF EXISTS `user_password`;

CREATE TABLE `user_password` (
                                 `id` int NOT NULL AUTO_INCREMENT,
                                 `encrypt_password` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL,
                                 `user_id` int NOT NULL,
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO `user_password` VALUES (1,'4QrcOUm6Wau+VuBX8g+IPg==',1),(2,'4QrcOUm6Wau+VuBX8g+IPg==',2);

