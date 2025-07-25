-- =========================
-- DROP TABLES IN FK ORDER
-- =========================

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS benefit_grade;
DROP TABLE IF EXISTS user_card_month_amount;
DROP TABLE IF EXISTS annual_cost;
DROP TABLE IF EXISTS benefit_limit;
DROP TABLE IF EXISTS partner;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS benefit;
DROP TABLE IF EXISTS grade;
DROP TABLE IF EXISTS user_card;
DROP TABLE IF EXISTS card;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS company;
DROP TABLE IF EXISTS discount;

SET FOREIGN_KEY_CHECKS = 1;

-- =========================
-- CREATE TABLES START
-- =========================



-- 카드사
CREATE TABLE `company` (
                           `company_id` BIGINT NOT NULL AUTO_INCREMENT,
                           `company_name` VARCHAR(10) NOT NULL,
                           PRIMARY KEY (`company_id`)
);

-- 사용자
CREATE TABLE `user` (
                        `user_id` BIGINT NOT NULL AUTO_INCREMENT,
                        `oauth_provider` VARCHAR(255) NOT NULL,
                        `oauth_id` VARCHAR(255) NOT NULL,
                        `user_name` VARCHAR(10) NOT NULL,
                        PRIMARY KEY (`user_id`)
);

-- 카드
CREATE TABLE `card` (
                        `card_id` BIGINT NOT NULL AUTO_INCREMENT,
                        `company_id` BIGINT NOT NULL,
                        `card_name` VARCHAR(100) NOT NULL,
                        `card_type` ENUM('CREDIT', 'CHECK') NOT NULL,
                        `card_image_url` VARCHAR(255) NOT NULL,
                        `card_issue_url` VARCHAR(255) NOT NULL,
                        PRIMARY KEY (`card_id`),
                        CONSTRAINT `FK_company_TO_card` FOREIGN KEY (`company_id`)
                            REFERENCES `company` (`company_id`)
                            ON DELETE CASCADE ON UPDATE CASCADE
);

-- 사용자별 카드
CREATE TABLE `user_card` (
                             `user_card_id` BIGINT NOT NULL AUTO_INCREMENT,
                             `user_id` BIGINT NOT NULL,
                             `card_id` BIGINT NOT NULL,
                             PRIMARY KEY (`user_card_id`),
                             CONSTRAINT `FK_user_TO_user_card` FOREIGN KEY (`user_id`)
                                 REFERENCES `user` (`user_id`)
                                 ON DELETE CASCADE ON UPDATE CASCADE,
                             CONSTRAINT `FK_card_TO_user_card` FOREIGN KEY (`card_id`)
                                 REFERENCES `card` (`card_id`)
                                 ON DELETE CASCADE ON UPDATE CASCADE
);

-- 실적 등급
CREATE TABLE `grade` (
                         `grade_id` BIGINT NOT NULL AUTO_INCREMENT,
                         `card_id` BIGINT NOT NULL,
                         `start` INT NULL,
                         `end` INT NULL,
                         `total_discount` INT NULL,
                         PRIMARY KEY (`grade_id`),
                         CONSTRAINT `FK_card_TO_grade` FOREIGN KEY (`card_id`)
                             REFERENCES `card` (`card_id`)
                             ON DELETE CASCADE ON UPDATE CASCADE
);

-- 혜택
CREATE TABLE `benefit` (
                           `benefit_id` BIGINT NOT NULL AUTO_INCREMENT,
                           `card_id` BIGINT NOT NULL,
                           `benefit_title` VARCHAR(255) NOT NULL,
                           `benefit_summary` VARCHAR(255) NOT NULL,
                           `benefit_description` LONGTEXT NOT NULL,
                           `benefit_icon_url` VARCHAR(255) NOT NULL,
                           `min_payment` INT NULL,
                           PRIMARY KEY (`benefit_id`),
                           CONSTRAINT `FK_card_TO_benefit` FOREIGN KEY (`card_id`)
                               REFERENCES `card` (`card_id`)
                               ON DELETE CASCADE ON UPDATE CASCADE
);

-- 카테고리
CREATE TABLE `category` (
                            `category_id` BIGINT NOT NULL AUTO_INCREMENT,
                            `benefit_id` BIGINT NOT NULL,
                            `category_name` VARCHAR(10) NOT NULL,
                            `pre_category_id` BIGINT NULL,
                            PRIMARY KEY (`category_id`),
                            CONSTRAINT `FK_benefit_TO_category` FOREIGN KEY (`benefit_id`)
                                REFERENCES `benefit` (`benefit_id`)
                                ON DELETE CASCADE ON UPDATE CASCADE,
                            CONSTRAINT `FK_category_TO_category` FOREIGN KEY (`pre_category_id`)
                                REFERENCES `category` (`category_id`)
                                ON DELETE SET NULL ON UPDATE CASCADE
);

-- 가맹점
CREATE TABLE `partner` (
                           `partner_id` BIGINT NOT NULL AUTO_INCREMENT,
                           `category_id` BIGINT NOT NULL,
                           `benefit_id` BIGINT NOT NULL,
                           `partner_name` VARCHAR(100) NOT NULL,
                           PRIMARY KEY (`partner_id`),
                           CONSTRAINT `FK_category_TO_partner` FOREIGN KEY (`category_id`)
                               REFERENCES `category` (`category_id`)
                               ON DELETE CASCADE ON UPDATE CASCADE,
                           CONSTRAINT `FK_benefit_TO_partner` FOREIGN KEY (`benefit_id`)
                               REFERENCES `benefit` (`benefit_id`)
                               ON DELETE CASCADE ON UPDATE CASCADE
);

-- 혜택 제한
CREATE TABLE `benefit_limit` (
                                 `benefit_limit_id` BIGINT NOT NULL AUTO_INCREMENT,
                                 `benefit_id` BIGINT NOT NULL,
                                 `limit_count_per_day` INT NULL,
                                 `limit_count_per_month` INT NULL,
                                 `limit_count_per_year` INT NULL,
                                 `limit_amount_per_pay` INT NULL,
                                 PRIMARY KEY (`benefit_limit_id`),
                                 CONSTRAINT `FK_benefit_TO_benefit_limit` FOREIGN KEY (`benefit_id`)
                                     REFERENCES `benefit` (`benefit_id`)
                                     ON DELETE CASCADE ON UPDATE CASCADE
);


-- 연회비
CREATE TABLE `annual_cost` (
                               `annual_cost_id` BIGINT NOT NULL AUTO_INCREMENT,
                               `card_id` BIGINT NOT NULL,
                               `brand_name` VARCHAR(10) NULL,
                               `annual_fee` INT NULL,
                               `co_annual_fee` INT NULL,
                               PRIMARY KEY (`annual_cost_id`),
                               CONSTRAINT `FK_card_TO_annual_cost` FOREIGN KEY (`card_id`)
                                   REFERENCES `card` (`card_id`)
                                   ON DELETE CASCADE ON UPDATE CASCADE
);

-- 할인
CREATE TABLE `discount` (
                            `discount_id` BIGINT NOT NULL AUTO_INCREMENT,
                            `type` ENUM('RATE', 'FIXED_AMOUNT') NOT NULL,
                            `amount` INT NOT NULL,
                            `limit_count` INT NULL,
                            `limit_amount` INT NULL,
                            PRIMARY KEY (`discount_id`)
);

-- 혜택별 실적별
CREATE TABLE `benefit_grade` (
                                 `benefit_grade_id` BIGINT NOT NULL AUTO_INCREMENT,
                                 `discount_id` BIGINT NOT NULL,
                                 `grade_id` BIGINT NOT NULL,
                                 `benefit_id` BIGINT NOT NULL,
                                 PRIMARY KEY (`benefit_grade_id`),
                                 CONSTRAINT `FK_discount_TO_benefit_grade` FOREIGN KEY (`discount_id`)
                                     REFERENCES `discount` (`discount_id`)
                                     ON DELETE CASCADE ON UPDATE CASCADE,
                                 CONSTRAINT `FK_grade_TO_benefit_grade` FOREIGN KEY (`grade_id`)
                                     REFERENCES `grade` (`grade_id`)
                                     ON DELETE CASCADE ON UPDATE CASCADE,
                                 CONSTRAINT `FK_benefit_TO_benefit_grade` FOREIGN KEY (`benefit_id`)
                                     REFERENCES `benefit` (`benefit_id`)
                                     ON DELETE CASCADE ON UPDATE CASCADE
);

-- 사용자의 월별 카드 사용량
CREATE TABLE `user_card_month_amount` (
                                          `user_card_month_amount_id` BIGINT NOT NULL AUTO_INCREMENT,
                                          `user_card_id` BIGINT NOT NULL,
                                          `month` CHAR(6) NOT NULL,
                                          `total_amount` BIGINT NOT NULL,
                                          PRIMARY KEY (`user_card_month_amount_id`),
                                          CONSTRAINT `FK_user_card_TO_user_card_month_amount` FOREIGN KEY (`user_card_id`)
                                              REFERENCES `user_card` (`user_card_id`)
                                              ON DELETE CASCADE ON UPDATE CASCADE
);