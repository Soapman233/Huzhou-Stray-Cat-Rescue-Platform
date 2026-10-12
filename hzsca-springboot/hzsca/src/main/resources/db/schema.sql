-- ============================================================
-- HZSCA 流浪猫救助与领养平台 —— 基础表结构（17 张表）
-- ------------------------------------------------------------
-- 说明：本文件由数据库实际结构导出，用于「新环境一键建库」。
--       应用启动时 DbBootstrap 会检测核心表，缺失时自动执行本脚本，
--       因此通常无需手动导入；如需手动执行：
--         mysql -uroot -p hzsca < schema.sql
--
-- 安全：全部使用 CREATE TABLE IF NOT EXISTS，**不会**删除或覆盖已有数据。
-- 注意：本文件仅含表结构（DDL）；演示数据由 seed_*.sql 提供。
-- ============================================================

USE hzsca;

CREATE TABLE IF NOT EXISTS `activity` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `date` datetime NOT NULL,
  `Location` varchar(255) NOT NULL,
  `imgurl` varchar(255) NOT NULL,
  `detail` text NOT NULL,
  `status` enum('upcoming','ended','ongoing') NOT NULL DEFAULT 'upcoming',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE IF NOT EXISTS `activity_participation` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activity_id` int NOT NULL,
  `participant_type` varchar(10) NOT NULL,
  `participant_id` bigint NOT NULL,
  `join_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` varchar(20) NOT NULL DEFAULT 'PENDING',
  `audit_time` datetime DEFAULT NULL,
  `auditor_id` bigint DEFAULT NULL,
  `audit_reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_participant` (`activity_id`,`participant_type`,`participant_id`),
  KEY `idx_activity_id` (`activity_id`)
) ENGINE=InnoDB AUTO_INCREMENT=93004 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE IF NOT EXISTS `admins` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `name` varchar(50) NOT NULL,
  `role` enum('admin','user') NOT NULL DEFAULT 'user',
  `avatar` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE IF NOT EXISTS `adopters` (
  `AdopterID` int NOT NULL AUTO_INCREMENT,
  `UserID` int NOT NULL,
  `Name` varchar(50) NOT NULL,
  `Age` int DEFAULT NULL,
  `IDCardNumber` varchar(18) NOT NULL,
  `Phone` varchar(15) NOT NULL,
  `Email` varchar(100) DEFAULT NULL,
  `Address` varchar(255) NOT NULL,
  `BirthDate` date DEFAULT NULL,
  PRIMARY KEY (`AdopterID`),
  KEY `UserID` (`UserID`),
  CONSTRAINT `adopters_ibfk_1` FOREIGN KEY (`UserID`) REFERENCES `users` (`UserID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE IF NOT EXISTS `adoption_assessment` (
  `AssessmentID` int NOT NULL AUTO_INCREMENT,
  `AdopterID` int NOT NULL,
  `Name` varchar(50) NOT NULL,
  `FamilyIncome` decimal(10,2) DEFAULT NULL,
  `HasOwnedPets` tinyint(1) DEFAULT NULL,
  `Smoker` tinyint(1) DEFAULT NULL,
  `Asthma` tinyint(1) DEFAULT NULL,
  `Allergy` tinyint(1) DEFAULT NULL,
  `LivingSpace` varchar(100) DEFAULT NULL,
  `PetFriendly` tinyint(1) DEFAULT NULL,
  `Status` enum('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`AssessmentID`),
  KEY `AdopterID` (`AdopterID`),
  CONSTRAINT `adoption_assessment_ibfk_1` FOREIGN KEY (`AdopterID`) REFERENCES `adopters` (`AdopterID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE IF NOT EXISTS `adoption_form` (
  `request_id` bigint NOT NULL,
  `family_income` decimal(10,2) DEFAULT NULL,
  `has_owned_pets` tinyint(1) DEFAULT NULL,
  `smoker` tinyint(1) DEFAULT NULL,
  `asthma` tinyint(1) DEFAULT NULL,
  `allergy` tinyint(1) DEFAULT NULL,
  `living_space` varchar(100) DEFAULT NULL,
  `pet_friendly` tinyint(1) DEFAULT NULL,
  `work_hours` int DEFAULT NULL,
  `rent_or_own` varchar(20) DEFAULT NULL,
  `family_members` int DEFAULT NULL,
  `child_in_home` tinyint(1) DEFAULT NULL,
  `accept_return_visit` tinyint(1) DEFAULT NULL,
  `remarks` text,
  PRIMARY KEY (`request_id`),
  CONSTRAINT `fk_adoption_form_request` FOREIGN KEY (`request_id`) REFERENCES `adoption_request` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE IF NOT EXISTS `adoption_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `request_id` bigint NOT NULL,
  `animal_id` int NOT NULL,
  `wx_user_id` bigint NOT NULL,
  `adopt_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `contract_url` varchar(255) DEFAULT NULL,
  `followup_plan` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_adoption_record_request` (`request_id`),
  KEY `idx_adoption_record_animal` (`animal_id`),
  KEY `idx_adoption_record_user` (`wx_user_id`),
  CONSTRAINT `fk_adoption_record_animal` FOREIGN KEY (`animal_id`) REFERENCES `animals` (`AnimalID`),
  CONSTRAINT `fk_adoption_record_request` FOREIGN KEY (`request_id`) REFERENCES `adoption_request` (`id`),
  CONSTRAINT `fk_adoption_record_user` FOREIGN KEY (`wx_user_id`) REFERENCES `wx_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE IF NOT EXISTS `adoption_request` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `animal_id` int NOT NULL,
  `wx_user_id` bigint DEFAULT NULL,
  `status` enum('PENDING','APPROVED','REJECTED','CANCELED') NOT NULL DEFAULT 'PENDING',
  `apply_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `audit_time` timestamp NULL DEFAULT NULL,
  `auditor_id` bigint DEFAULT NULL,
  `audit_reason` varchar(500) DEFAULT NULL,
  `score` int DEFAULT NULL,
  `risk_level` varchar(10) DEFAULT NULL,
  `risk_reasons` text,
  `applicant_type` varchar(10) NOT NULL DEFAULT 'WX',
  `applicant_id` bigint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_adoption_request_animal` (`animal_id`),
  KEY `idx_adoption_request_user` (`wx_user_id`),
  CONSTRAINT `fk_adoption_request_animal` FOREIGN KEY (`animal_id`) REFERENCES `animals` (`AnimalID`),
  CONSTRAINT `fk_adoption_request_user` FOREIGN KEY (`wx_user_id`) REFERENCES `wx_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=90009 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE IF NOT EXISTS `animals` (
  `AnimalID` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(50) NOT NULL,
  `Species` varchar(50) NOT NULL,
  `Breed` varchar(50) DEFAULT NULL,
  `Age` float DEFAULT NULL,
  `Gender` enum('雄性','雌性','未知') NOT NULL,
  `HealthStatus` varchar(100) DEFAULT NULL,
  `Description` text,
  `ImageURL` varchar(255) DEFAULT NULL,
  `Source` varchar(100) DEFAULT NULL,
  `Status` enum('ADOPTABLE','PENDING','ADOPTED','MEDICAL','HOLD') NOT NULL DEFAULT 'ADOPTABLE',
  PRIMARY KEY (`AnimalID`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE IF NOT EXISTS `cat_traits` (
  `cat_id` int NOT NULL,
  `personality` varchar(20) NOT NULL DEFAULT 'ANY',
  `activity_level` tinyint NOT NULL DEFAULT '3',
  `affection_level` tinyint NOT NULL DEFAULT '3',
  `child_friendly` tinyint(1) NOT NULL DEFAULT '1',
  `pet_friendly` tinyint(1) NOT NULL DEFAULT '1',
  `shedding_level` tinyint NOT NULL DEFAULT '3',
  `nocturnal` tinyint(1) NOT NULL DEFAULT '0',
  `space_need_level` tinyint NOT NULL DEFAULT '2',
  `beginner_friendly` tinyint(1) NOT NULL DEFAULT '1',
  `special_care_needed` tinyint(1) NOT NULL DEFAULT '0',
  `environment_preference` varchar(20) NOT NULL DEFAULT 'ANY',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`cat_id`),
  CONSTRAINT `fk_cat_traits_cat` FOREIGN KEY (`cat_id`) REFERENCES `animals` (`AnimalID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE IF NOT EXISTS `match_weights` (
  `weight_key` varchar(50) NOT NULL,
  `weight_value` decimal(6,3) NOT NULL,
  PRIMARY KEY (`weight_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE IF NOT EXISTS `rescue_report` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `wx_user_id` bigint DEFAULT NULL,
  `lat` decimal(10,6) DEFAULT NULL,
  `lng` decimal(10,6) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `description` text,
  `image_urls` text,
  `urgency` tinyint DEFAULT '1',
  `status` enum('OPEN','IN_PROGRESS','CLOSED') NOT NULL DEFAULT 'OPEN',
  `handler_id` bigint DEFAULT NULL,
  `handle_result` varchar(500) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT NULL,
  `reporter_type` varchar(10) NOT NULL DEFAULT 'WX',
  `reporter_id` bigint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_rescue_report_user` (`wx_user_id`),
  KEY `idx_rescue_report_status` (`status`),
  CONSTRAINT `fk_rescue_report_user` FOREIGN KEY (`wx_user_id`) REFERENCES `wx_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=91005 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE IF NOT EXISTS `user_adoption_profile` (
  `user_id` bigint NOT NULL,
  `residence_city` varchar(50) NOT NULL,
  `housing_type` varchar(20) NOT NULL,
  `home_size_level` tinyint NOT NULL DEFAULT '2',
  `has_children` tinyint(1) NOT NULL DEFAULT '0',
  `has_other_pets` tinyint(1) NOT NULL DEFAULT '0',
  `allergy` tinyint(1) NOT NULL DEFAULT '0',
  `max_shedding_level` tinyint NOT NULL DEFAULT '5',
  `daily_companionship_hours` tinyint NOT NULL DEFAULT '0',
  `work_schedule` varchar(20) NOT NULL DEFAULT 'DAY',
  `budget_level` tinyint NOT NULL DEFAULT '2',
  `experience_level` tinyint NOT NULL DEFAULT '0',
  `personality_preference` varchar(20) NOT NULL DEFAULT 'ANY',
  `accept_special_care` tinyint(1) NOT NULL DEFAULT '0',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_user_adoption_profile_user` FOREIGN KEY (`user_id`) REFERENCES `wx_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE IF NOT EXISTS `user_behavior` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `wx_user_id` bigint NOT NULL,
  `event` varchar(30) NOT NULL,
  `animal_id` int DEFAULT NULL,
  `request_id` bigint DEFAULT NULL,
  `report_id` bigint DEFAULT NULL,
  `extra` text,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_behavior_user` (`wx_user_id`),
  KEY `idx_user_behavior_event` (`event`),
  KEY `idx_user_behavior_animal` (`animal_id`),
  KEY `idx_user_behavior_request` (`request_id`),
  KEY `fk_user_behavior_report` (`report_id`),
  CONSTRAINT `fk_user_behavior_animal` FOREIGN KEY (`animal_id`) REFERENCES `animals` (`AnimalID`),
  CONSTRAINT `fk_user_behavior_report` FOREIGN KEY (`report_id`) REFERENCES `rescue_report` (`id`),
  CONSTRAINT `fk_user_behavior_request` FOREIGN KEY (`request_id`) REFERENCES `adoption_request` (`id`),
  CONSTRAINT `fk_user_behavior_user` FOREIGN KEY (`wx_user_id`) REFERENCES `wx_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE IF NOT EXISTS `users` (
  `UserID` int NOT NULL AUTO_INCREMENT,
  `Username` varchar(50) NOT NULL,
  `Password` varchar(255) NOT NULL,
  `Phone` varchar(15) DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  `Gender` int NOT NULL,
  `Profile` text,
  `role` enum('admin','user','guest') NOT NULL DEFAULT 'user',
  `avatar` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`UserID`)
) ENGINE=InnoDB AUTO_INCREMENT=8004 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE IF NOT EXISTS `volunteer_application` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `applicant_type` varchar(10) NOT NULL,
  `applicant_id` bigint NOT NULL,
  `name` varchar(50) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `age` int DEFAULT NULL,
  `experience` text,
  `motivation` text,
  `status` varchar(20) NOT NULL DEFAULT 'PENDING',
  `apply_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `audit_time` datetime DEFAULT NULL,
  `auditor_id` bigint DEFAULT NULL,
  `audit_reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_vol_applicant` (`applicant_type`,`applicant_id`),
  KEY `idx_vol_status` (`status`),
  KEY `idx_vol_apply_time` (`apply_time`)
) ENGINE=InnoDB AUTO_INCREMENT=92006 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE IF NOT EXISTS `wx_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `openid` varchar(64) NOT NULL,
  `unionid` varchar(64) DEFAULT NULL,
  `nickname` varchar(100) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `gender` tinyint DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `province` varchar(50) DEFAULT NULL,
  `country` varchar(50) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `last_login_time` timestamp NULL DEFAULT NULL,
  `role` varchar(20) NOT NULL DEFAULT 'user',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_wx_user_openid` (`openid`)
) ENGINE=InnoDB AUTO_INCREMENT=9004 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
