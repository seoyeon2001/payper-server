START TRANSACTION;
-- =========================
-- DROP TABLES IN FK ORDER
-- =========================

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS user_card_month_amount;
DROP TABLE IF EXISTS benefit_grade_discount;
DROP TABLE IF EXISTS benefit_category;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS benefit;
DROP TABLE IF EXISTS grade;
DROP TABLE IF EXISTS user_card;
DROP TABLE IF EXISTS card;
DROP TABLE IF EXISTS user;

SET FOREIGN_KEY_CHECKS = 1;


-- =========================
-- CREATE TABLES START
-- =========================

-- 사용자
CREATE TABLE `user` (
                        `user_id` INT NOT NULL AUTO_INCREMENT,
                        `oauth_provider` VARCHAR(255) NOT NULL,
                        `oauth_id` VARCHAR(255) NOT NULL,
                        `user_name` VARCHAR(10) NOT NULL,
                        `connected_id` VARCHAR(255) NULL,
                        PRIMARY KEY (`user_id`)
);

-- 카드
CREATE TABLE `card` (
                        `card_id` INT NOT NULL AUTO_INCREMENT,
                        `card_name` VARCHAR(100) NOT NULL,
                        `card_type` ENUM('CREDIT', 'CHECK') NOT NULL,
                        `card_image_url` VARCHAR(255) NOT NULL,
                        `card_issue_url` VARCHAR(255) NOT NULL,
                        `company_name` VARCHAR(10) NOT NULL,
                        `annual_fee` VARCHAR(255) default '',
                        PRIMARY KEY (`card_id`)
);

-- 사용자별 카드
CREATE TABLE `user_card` (
                             `user_card_id` INT NOT NULL AUTO_INCREMENT,
                             `user_id` INT NOT NULL,
                             `card_id` INT NOT NULL,
                             PRIMARY KEY (`user_card_id`),
                             CONSTRAINT `FK_user_TO_user_card` FOREIGN KEY (`user_id`)
                                 REFERENCES `user` (`user_id`),
                             CONSTRAINT `FK_card_TO_user_card` FOREIGN KEY (`card_id`)
                                 REFERENCES `card` (`card_id`)
);

-- 실적 등급
CREATE TABLE `grade` (
                         `grade_id` INT NOT NULL AUTO_INCREMENT,
                         `card_id` INT NOT NULL,
                         `start` BIGINT DEFAULT 0,
                         `end` BIGINT NULL,
                         `total_discount` BIGINT NULL,
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
                           PRIMARY KEY (`benefit_id`),
                           CONSTRAINT `FK_card_TO_benefit` FOREIGN KEY (`card_id`)
                               REFERENCES `card` (`card_id`)
);

-- 카테고리
CREATE TABLE `category` (
                            `category_id` INT NOT NULL AUTO_INCREMENT,
                            `category_name` VARCHAR(10) NOT NULL,
                            `pre_category_id` INT NULL,
                            PRIMARY KEY (`category_id`),
                            CONSTRAINT `FK_category_TO_category` FOREIGN KEY (`pre_category_id`)
                                REFERENCES `category` (`category_id`)
);

-- 혜택_카테고리
CREATE TABLE `benefit_category` (
                           `benefit_category_id` INT NOT NULL AUTO_INCREMENT,
                           `benefit_id` INT NOT NULL,
                           `category_id` INT NOT NULL,
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
                                 `type` ENUM('RATE', 'FIXED_AMOUNT') NOT NULL,
                                 `amount` BIGINT NOT NULL,
                                 `limit_count` BIGINT NULL,
                                 `limit_amount` BIGINT NULL,
                                 `min_payment` BIGINT DEFAULT 0,
                                 PRIMARY KEY (`benefit_grade_id`),
                                 CONSTRAINT `FK_grade_TO_benefit_grade_discount` FOREIGN KEY (`grade_id`)
                                     REFERENCES `grade` (`grade_id`),
                                 CONSTRAINT `FK_benefit_TO_benefit_grade_discount` FOREIGN KEY (`benefit_id`)
                                     REFERENCES `benefit` (`benefit_id`)
);


-- 사용자의 월별 카드 사용량
CREATE TABLE `user_card_month_amount` (
                                          `user_card_month_amount_id` INT NOT NULL AUTO_INCREMENT,
                                          `user_card_id` INT NOT NULL,
                                          `month` CHAR(6) NOT NULL,
                                          `total_amount` BIGINT NOT NULL,
                                          PRIMARY KEY (`user_card_month_amount_id`),
                                          CONSTRAINT `FK_user_card_TO_user_card_month_amount` FOREIGN KEY (`user_card_id`)
                                              REFERENCES `user_card` (`user_card_id`)
);
COMMIT;