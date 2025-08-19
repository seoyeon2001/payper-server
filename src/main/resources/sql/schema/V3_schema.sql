START TRANSACTION;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS user_card_transaction;
DROP TABLE IF EXISTS benefit_category;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS benefit;
DROP TABLE IF EXISTS user_card;
DROP TABLE IF EXISTS card;
DROP TABLE IF EXISTS card_company;
DROP TABLE IF EXISTS fcm_token;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS benefit_partner;
DROP TABLE IF EXISTS partner;

SET FOREIGN_KEY_CHECKS = 1;

-- 사용자
CREATE TABLE `user` (
                        `user_id` INT NOT NULL AUTO_INCREMENT,
                        `oauth_provider` VARCHAR(255) NOT NULL,
                        `oauth_id` VARCHAR(255) NOT NULL,
                        `nickname` VARCHAR(10) NOT NULL,
                        `connected_id` VARCHAR(255),
                        `role` ENUM('ROLE_USER', 'ROLE_ADMIN') NOT NULL,
                        `is_deleted` BOOLEAN DEFAULT FALSE,
                        `created_at` DATETIME NOT NULL DEFAULT NOW(),
                        `deleted_at` DATETIME NULL,
                        `last_modified_at` DATETIME NULL,
                        PRIMARY KEY (`user_id`)
);

-- FCM TOKEN
CREATE TABLE `fcm_token` (
                           user_id INT NOT NULL,
                           token VARCHAR(255) NOT NULL,
                           status ENUM('ACTIVE','INACTIVE') DEFAULT 'ACTIVE',
                           last_modified_at DATETIME DEFAULT NOW() ON UPDATE NOW(),
                           PRIMARY KEY (user_id, token),
                            CONSTRAINT `FK_user_TO_fcm_token` FOREIGN KEY (`user_id`)
                            REFERENCES `user` (`user_id`)
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
                        `codef_card_name` VARCHAR(100) UNIQUE,
                        `card_type` ENUM('CREDIT', 'CHECK') NOT NULL,
                        `card_image_url` VARCHAR(255) NOT NULL,
                        `card_issue_url` VARCHAR(255),
                        `annual_fee` VARCHAR(255) DEFAULT '',
                        `prev_month_spending` BIGINT DEFAULT 0,
                        `company_id` INT NOT NULL,
                        `is_deleted` BOOLEAN DEFAULT FALSE,
                        `is_deactivate` BOOLEAN DEFAULT FALSE,
                        `created_at` DATETIME NOT NULL DEFAULT NOW(),
                        `deleted_at` DATETIME NULL,
                        `deactivate_at` DATETIME NULL,
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
                             `last3` CHAR(3),               -- 카드번호 끝 3자리
                             `is_deleted` BOOLEAN DEFAULT FALSE,
                             `is_deactivate` BOOLEAN DEFAULT FALSE,
                             `created_at` DATETIME NOT NULL DEFAULT NOW(),
                             `deleted_at` DATETIME NULL,
                             `deactivate_at` DATETIME NULL,
                             `last_modified_at` DATETIME NULL,
                             PRIMARY KEY (`user_card_id`),
                             CONSTRAINT `FK_user_TO_user_card` FOREIGN KEY (`user_id`)
                                 REFERENCES `user` (`user_id`),
                             CONSTRAINT `FK_card_TO_user_card` FOREIGN KEY (`card_id`)
                                 REFERENCES `card` (`card_id`),
                             CONSTRAINT `UK_user_card_userid_cardid` UNIQUE (`user_id`, `card_id`)
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
                            `is_deleted` BOOLEAN DEFAULT FALSE,
                            `created_at` DATETIME NOT NULL DEFAULT NOW(),
                            `deleted_at` DATETIME NULL,
                            `last_modified_at` DATETIME NULL,
                            PRIMARY KEY (`category_id`)
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

CREATE TABLE `user_card_transaction` (
                                         `user_card_transaction_id`	INT	NOT NULL AUTO_INCREMENT,
                                         `user_card_id`	INT	NOT NULL,
                                         `partner_id`	INT NULL,
                                         `res_used_date`	VARCHAR(255)	NOT NULL,
                                         `res_used_time`	VARCHAR(255)	NOT NULL,
                                         `res_card_no`	VARCHAR(255)	NULL,
                                         `res_card_no1`	VARCHAR(255)	NULL,
                                         `res_card_name`	VARCHAR(255)	NOT NULL,
                                         `res_member_store_name`	VARCHAR(255)	NULL,
                                         `res_used_amount`	VARCHAR(255)	NOT NULL,
                                         `res_payment_type`	VARCHAR(255)	NOT NULL,
                                         `res_installment_month`	VARCHAR(255)	NULL,
                                         `res_approval_no`	VARCHAR(255)	NULL,
                                         `res_payment_due_date`	VARCHAR(255)	NULL,
                                         `res_home_foreign_type`	VARCHAR(255)	NULL,
                                         `res_member_store_corp_no`	VARCHAR(255)	NULL,
                                         `res_member_store_type`	VARCHAR(255)	NULL,
                                         `res_member_store_tel_no`	VARCHAR(255)	NULL,
                                         `res_member_store_addr`	VARCHAR(255)	NULL,
                                         `res_member_store_no`	VARCHAR(255)	NULL,
                                         `res_cancel_yn`	VARCHAR(255)	NULL,
                                         `res_cancel_amount`	VARCHAR(255)	NULL,
                                         `res_vat`	VARCHAR(255)	NULL,
                                         `res_cash_back`	VARCHAR(255)	NULL,
                                         `res_krw_amt`	VARCHAR(255)	NULL,
                                         `comm_start_date`	VARCHAR(255)	NULL,
                                         `comm_end_date`	VARCHAR(255)	NULL,
                                         `res_account_currency`	VARCHAR(255)	NULL,
                                         `is_deleted` BOOLEAN DEFAULT FALSE,
                                         `created_at` DATETIME NOT NULL DEFAULT NOW(),
                                         `deleted_at` DATETIME NULL,
                                         `last_modified_at` DATETIME NULL,
                                         PRIMARY KEY (`user_card_transaction_id`),
                                         CONSTRAINT `FK_benefit_TO_user_card_transaction` FOREIGN KEY (`user_card_id`)
                                             REFERENCES `user_card` (`user_card_id`),
                                         CONSTRAINT `FK_partner_TO_user_card_transaction` FOREIGN KEY (`partner_id`)
                                             REFERENCES `partner` (`partner_id`)
);

CREATE TABLE report (
                        report_id  INT AUTO_INCREMENT PRIMARY KEY,
                        user_id    INT NOT NULL,
                        month      DATE NOT NULL,              -- 해당 월의 1일로 저장 (예: 2025-07-01)
                        payload    JSON NOT NULL,              -- 원본 JSON
                        created_at DATETIME NOT NULL DEFAULT NOW(),
                        UNIQUE KEY uq_user_month (user_id, month),
                        CONSTRAINT fk_user_report FOREIGN KEY (user_id)
                            REFERENCES `user` (user_id)
);

CREATE INDEX idx_benefit_category_lookup ON benefit_category(benefit_id, is_deleted);

drop table if exists search_card;
create table search_card as
SELECT
    c0.card_id, c0.company_id, c0.card_name, c0.card_type,
    c0.card_image_url, c0.card_issue_url, c0.annual_fee, c0.prev_month_spending,
    ca0.category_name, cc0.company_name,
    b0.benefit_title,b0.benefit_summary,b0.benefit_description
FROM card c0
         LEFT JOIN card_company cc0 ON c0.company_id = cc0.company_id
         LEFT JOIN benefit b0 ON c0.card_id = b0.card_id and b0.is_deleted=false
         LEFT JOIN benefit_category bc0 ON b0.benefit_id = bc0.benefit_id and bc0.is_deleted=false
         LEFT JOIN category ca0 ON ca0.category_id = bc0.category_id
WHERE c0.is_deleted=false;

COMMIT;

