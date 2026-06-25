START TRANSACTION;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS user_card_transaction;
DROP TABLE IF EXISTS benefit_grade_discount;
DROP TABLE IF EXISTS benefit_category;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS benefit;
DROP TABLE IF EXISTS grade;
DROP TABLE IF EXISTS user_card;
DROP TABLE IF EXISTS card;
DROP TABLE IF EXISTS card_company;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS benefit_partner;
DROP TABLE IF EXISTS partner;

SET FOREIGN_KEY_CHECKS = 1;

-- 사용자
CREATE TABLE `user` (
                        `user_id` INT NOT NULL AUTO_INCREMENT,
                        `username` VARCHAR(50) NOT NULL UNIQUE,
                        `password` VARCHAR(255) NOT NULL,
                        `nickname` VARCHAR(10) NOT NULL,
                        `connected_id` VARCHAR(255),
                        `role` ENUM('ROLE_USER', 'ROLE_ADMIN') NOT NULL,
                        `is_deleted` BOOLEAN DEFAULT FALSE,
                        `created_at` DATETIME NOT NULL DEFAULT NOW(),
                        `deleted_at` DATETIME NULL,
                        `last_modified_at` DATETIME NULL,
                        PRIMARY KEY (`user_id`)
);

-- 카드사
CREATE TABLE `card_company` (
                                `company_id` INT NOT NULL AUTO_INCREMENT,
                                `company_name` VARCHAR(255) UNIQUE,
                                `is_deleted` BOOLEAN DEFAULT FALSE,
                                `created_at` DATETIME NOT NULL DEFAULT NOW(),
                                `deleted_at` DATETIME NULL,
                                `last_modified_at` DATETIME NULL,
                                PRIMARY KEY (`company_id`)
);

-- 카드
CREATE TABLE `card` (
                        `card_id` INT NOT NULL AUTO_INCREMENT,
                        `card_name` VARCHAR(100) UNIQUE,
                        `card_type` ENUM('CREDIT', 'CHECK') NOT NULL,
                        `card_image_url` VARCHAR(255) NOT NULL,
                        `card_issue_url` VARCHAR(255),
                        `annual_fee` VARCHAR(255) DEFAULT '',
                        `company_id` INT NOT NULL,
                        `is_deleted` BOOLEAN DEFAULT FALSE,
                        `created_at` DATETIME NOT NULL DEFAULT NOW(),
                        `deleted_at` DATETIME NULL,
                        `last_modified_at` DATETIME NULL,
                        PRIMARY KEY (`card_id`),
                        CONSTRAINT `FK_card_company_TO_card` FOREIGN KEY (`company_id`)
                            REFERENCES `card_company` (`company_id`)
);

-- 사용자별 카드
CREATE TABLE `user_card` (
                             `user_card_id` INT NOT NULL AUTO_INCREMENT,
                             `user_id` INT NOT NULL,
                             `card_id` INT NOT NULL,
                             `is_deleted` BOOLEAN DEFAULT FALSE,
                             `created_at` DATETIME NOT NULL DEFAULT NOW(),
                             `deleted_at` DATETIME NULL,
                             `last_modified_at` DATETIME NULL,
                             PRIMARY KEY (`user_card_id`),
                             CONSTRAINT `FK_user_TO_user_card` FOREIGN KEY (`user_id`)
                                 REFERENCES `user` (`user_id`),
                             CONSTRAINT `FK_card_TO_user_card` FOREIGN KEY (`card_id`)
                                 REFERENCES `card` (`card_id`),
                             CONSTRAINT `UK_user_card_userid_cardid` UNIQUE (`user_id`, `card_id`)
);

-- 실적 등급
CREATE TABLE `grade` (
                         `grade_id` INT NOT NULL AUTO_INCREMENT,
                         `card_id` INT NOT NULL,
                         `start` BIGINT DEFAULT 0,
                         `total_discount` BIGINT,
                         `is_deleted` BOOLEAN DEFAULT FALSE,
                         `created_at` DATETIME NOT NULL DEFAULT NOW(),
                         `deleted_at` DATETIME NULL,
                         `last_modified_at` DATETIME NULL,
                         PRIMARY KEY (`grade_id`),
                         CONSTRAINT `FK_card_TO_grade` FOREIGN KEY (`card_id`)
                             REFERENCES `card` (`card_id`)
);

-- 혜택
CREATE TABLE `benefit` (
                           `benefit_id` INT NOT NULL AUTO_INCREMENT,
                           `card_id` INT NOT NULL,
                           `benefit_title` VARCHAR(255) NOT NULL,
                           `benefit_summary` VARCHAR(255) NOT NULL,
                           `benefit_description` LONGTEXT NOT NULL,
                           `benefit_icon_url` VARCHAR(255) NOT NULL,
                           `is_deleted` BOOLEAN DEFAULT FALSE,
                           `created_at` DATETIME NOT NULL DEFAULT NOW(),
                           `deleted_at` DATETIME NULL,
                           `last_modified_at` DATETIME NULL,
                           PRIMARY KEY (`benefit_id`),
                           CONSTRAINT `FK_card_TO_benefit` FOREIGN KEY (`card_id`)
                               REFERENCES `card` (`card_id`)
);

-- 카테고리
CREATE TABLE `category` (
                            `category_id` INT NOT NULL AUTO_INCREMENT,
                            `category_name` VARCHAR(255) UNIQUE,
                            `category_image_url` VARCHAR(255),
                            `pre_category_id` INT,
                            `is_deleted` BOOLEAN DEFAULT FALSE,
                            `created_at` DATETIME NOT NULL DEFAULT NOW(),
                            `deleted_at` DATETIME NULL,
                            `last_modified_at` DATETIME NULL,
                            PRIMARY KEY (`category_id`),
                            CONSTRAINT `FK_category_TO_category` FOREIGN KEY (`pre_category_id`)
                                REFERENCES `category` (`category_id`)
);

-- 파트너
CREATE TABLE `partner` (
                           `partner_id` INT NOT NULL AUTO_INCREMENT,
                           `category_id` INT NOT NULL,
                           `partner_name` VARCHAR(50) UNIQUE,
                           `partner_image_url` VARCHAR(255),
                           `is_deleted` BOOLEAN DEFAULT FALSE,
                           `created_at` DATETIME NOT NULL DEFAULT NOW(),
                           `deleted_at` DATETIME NULL,
                           `last_modified_at` DATETIME NULL,
                           PRIMARY KEY (`partner_id`),
                           CONSTRAINT `FK_category_TO_partner` FOREIGN KEY (`category_id`)
                               REFERENCES `category` (`category_id`)
);

-- 혜택_카테고리
CREATE TABLE `benefit_category` (
                                    `benefit_category_id` INT NOT NULL AUTO_INCREMENT,
                                    `benefit_id` INT NOT NULL,
                                    `category_id` INT NOT NULL,
                                    `is_deleted` BOOLEAN DEFAULT FALSE,
                                    `created_at` DATETIME NOT NULL DEFAULT NOW(),
                                    `deleted_at` DATETIME NULL,
                                    `last_modified_at` DATETIME NULL,
                                    PRIMARY KEY (`benefit_category_id`),
                                    CONSTRAINT `FK_benefit_TO_benefit_category` FOREIGN KEY (`benefit_id`)
                                        REFERENCES `benefit` (`benefit_id`)
                                        ON DELETE CASCADE ON UPDATE CASCADE,
                                    CONSTRAINT `FK_category_TO_benefit_category` FOREIGN KEY (`category_id`)
                                        REFERENCES `category` (`category_id`)
);

-- 혜택별 실적별 할인
CREATE TABLE `benefit_grade_discount` (
                                          `benefit_grade_id` INT NOT NULL AUTO_INCREMENT,
                                          `grade_id` INT NOT NULL,
                                          `benefit_id` INT NOT NULL,
                                          `type` ENUM('RATE', 'FIXED_AMOUNT', 'UNKNOWN') NOT NULL,
                                          `amount` BIGINT NOT NULL,
                                          `limit_count` BIGINT,
                                          `limit_amount` BIGINT,
                                          `min_payment` BIGINT DEFAULT 0,
                                          `is_deleted` BOOLEAN DEFAULT FALSE,
                                          `created_at` DATETIME NOT NULL DEFAULT NOW(),
                                          `deleted_at` DATETIME NULL,
                                          `last_modified_at` DATETIME NULL,
                                          PRIMARY KEY (`benefit_grade_id`),
                                          CONSTRAINT `FK_grade_TO_benefit_grade_discount` FOREIGN KEY (`grade_id`)
                                              REFERENCES `grade` (`grade_id`),
                                          CONSTRAINT `FK_benefit_TO_benefit_grade_discount` FOREIGN KEY (`benefit_id`)
                                              REFERENCES `benefit` (`benefit_id`)
);

-- 사용자 카드 결제 내역
CREATE TABLE `user_card_transaction` (
                                         `user_card_transaction_id` INT NOT NULL AUTO_INCREMENT,
                                         `user_card_id` INT NOT NULL,
                                         `amount` BIGINT NOT NULL,
                                         `approved_at` DATETIME NOT NULL,
                                         `is_deleted` BOOLEAN DEFAULT FALSE,
                                         `created_at` DATETIME NOT NULL DEFAULT NOW(),
                                         `deleted_at` DATETIME NULL,
                                         `last_modified_at` DATETIME NULL,
                                         PRIMARY KEY (`user_card_transaction_id`),
                                         CONSTRAINT `FK_user_card_TO_transaction` FOREIGN KEY (`user_card_id`)
                                             REFERENCES `user_card` (`user_card_id`)
);

-- 혜택_가맹점
CREATE TABLE `benefit_partner` (
                                        `benefit_partner_id` INT NOT NULL AUTO_INCREMENT,
                                        `benefit_id`         INT NOT NULL,
                                        `partner_id`         INT NOT NULL,
                                        `is_deleted` BOOLEAN DEFAULT FALSE,
                                        `created_at` DATETIME NOT NULL DEFAULT NOW(),
                                        `deleted_at` DATETIME NULL,
                                        `last_modified_at` DATETIME NULL,
                                        PRIMARY KEY (`benefit_partner_id`),
                                        CONSTRAINT `FK_benefit_TO_benefit_partner` FOREIGN KEY (`benefit_id`)
                                            REFERENCES `benefit` (`benefit_id`),
                                        CONSTRAINT `FK_partner_TO_benefit_partner` FOREIGN KEY (`partner_id`)
                                            REFERENCES `partner` (`partner_id`)
);
COMMIT;
