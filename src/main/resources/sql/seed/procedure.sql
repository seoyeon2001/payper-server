-- DELIMITER 설정
DELIMITER $$

/*
 * 1) category
 */
DROP PROCEDURE IF EXISTS seed_category$$
CREATE PROCEDURE seed_category(IN p_count INT)
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= p_count
        DO
            INSERT INTO `category` (category_name,
                                    category_image_url,
                                    is_deleted,
                                    created_at)
            VALUES (CONCAT('category_', i),
                    CONCAT('https://img.example/category_', i, '.jpg'),
                    FALSE,
                    NOW());
            SET i = i + 1;
        END WHILE;
END$$

/*
 * 2) card_company
 */
DROP PROCEDURE IF EXISTS seed_card_company$$
CREATE PROCEDURE seed_card_company(IN p_count INT)
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= p_count
        DO
            INSERT INTO `card_company` (company_name,
                                        is_deleted,
                                        created_at)
            VALUES (CONCAT('CardCo_', i),
                    FALSE,
                    NOW());
            SET i = i + 1;
        END WHILE;
END$$

/*
 * 3) user
 */
DROP PROCEDURE IF EXISTS seed_user$$
CREATE PROCEDURE seed_user(IN p_count INT)
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= p_count
        DO
            INSERT INTO `user` (oauth_provider,
                                oauth_id,
                                nickname,
                                connected_id,
                                role,
                                is_deleted,
                                created_at)
            VALUES (CONCAT('oauth_provider_', i),
                    CONCAT('oauth_id_', i),
                    LEFT(CONCAT('nick', i), 10),
                    NULL,
                    'ROLE_USER',
                    FALSE,
                    NOW());
            SET i = i + 1;
        END WHILE;
END$$

/*
 * 4) card (참조: card_company)
 */
DROP PROCEDURE IF EXISTS seed_card$$
CREATE PROCEDURE seed_card(IN p_count INT)
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE company_cnt INT;
    SELECT COUNT(*) INTO company_cnt FROM `card_company`;
    IF company_cnt = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No card_company rows exist. Run seed_card_company first.';
    END IF;

    WHILE i <= p_count
        DO
            INSERT INTO `card` (card_name,
                                codef_card_name,
                                card_type,
                                card_image_url,
                                card_issue_url,
                                annual_fee,
                                prev_month_spending,
                                company_id,
                                is_deleted,
                                is_deactivate,
                                created_at)
            VALUES (CONCAT('CardName_', i),
                    CONCAT('CodefCard_', i),
                    'CREDIT',
                    CONCAT('https://img.example/card_', i, '.jpg'),
                    CONCAT('https://issue.example/card_', i, '.html'),
                    '',
                    0,
                    ((i - 1) % company_cnt) + 1,
                    FALSE,
                    FALSE,
                    NOW());
            SET i = i + 1;
        END WHILE;
END$$

/*
 * 5) partner (참조: category)
 */
DROP PROCEDURE IF EXISTS seed_partner$$
CREATE PROCEDURE seed_partner(IN p_count INT)
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE category_cnt INT;
    SELECT COUNT(*) INTO category_cnt FROM `category`;
    IF category_cnt = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No category rows exist. Run seed_category first.';
    END IF;

    WHILE i <= p_count
        DO
            INSERT INTO `partner` (category_id,
                                   partner_name,
                                   partner_image_url,
                                   is_deleted,
                                   created_at)
            VALUES (((i - 1) % category_cnt) + 1,
                    CONCAT('Partner_', i),
                    CONCAT('https://img.example/partner_', i, '.jpg'),
                    FALSE,
                    NOW());
            SET i = i + 1;
        END WHILE;
END$$

/*
 * 6) benefit (참조: card)
 */
DROP PROCEDURE IF EXISTS seed_benefit$$
CREATE PROCEDURE seed_benefit(IN p_count INT)
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE card_cnt INT;
    SELECT COUNT(*) INTO card_cnt FROM `card`;
    IF card_cnt = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No card rows exist. Run seed_card first.';
    END IF;

    WHILE i <= p_count
        DO
            INSERT INTO `benefit` (card_id,
                                   benefit_title,
                                   benefit_summary,
                                   benefit_description,
                                   benefit_icon_url,
                                   is_deleted,
                                   created_at)
            VALUES (((i - 1) % card_cnt) + 1,
                    CONCAT('Benefit Title ', i),
                    CONCAT('Summary ', i),
                    CONCAT('Detailed description for benefit ', i),
                    CONCAT('https://img.example/benefit_', i, '.png'),
                    FALSE,
                    NOW());
            SET i = i + 1;
        END WHILE;
END$$

/*
 * 7) benefit_category (참조: benefit, category)
 */
DROP PROCEDURE IF EXISTS seed_benefit_category$$
CREATE PROCEDURE seed_benefit_category(IN p_count INT)
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE benefit_cnt INT;
    DECLARE category_cnt INT;
    SELECT COUNT(*) INTO benefit_cnt FROM `benefit`;
    SELECT COUNT(*) INTO category_cnt FROM `category`;
    IF benefit_cnt = 0 OR category_cnt = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT =
                'Need benefit and category rows. Run seed_benefit and seed_category first.';
    END IF;

    WHILE i <= p_count
        DO
            INSERT INTO `benefit_category` (benefit_id,
                                            category_id,
                                            is_deleted,
                                            created_at)
            VALUES (((i - 1) % benefit_cnt) + 1,
                    ((i - 1) % category_cnt) + 1,
                    FALSE,
                    NOW());
            SET i = i + 1;
        END WHILE;
END$$

/*
 * 8) benefit_partner (참조: benefit, partner)
 */
DROP PROCEDURE IF EXISTS seed_benefit_partner$$
CREATE PROCEDURE seed_benefit_partner(IN p_count INT)
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE benefit_cnt INT;
    DECLARE partner_cnt INT;
    SELECT COUNT(*) INTO benefit_cnt FROM `benefit`;
    SELECT COUNT(*) INTO partner_cnt FROM `partner`;
    IF benefit_cnt = 0 OR partner_cnt = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT =
                'Need benefit and partner rows. Run seed_benefit and seed_partner first.';
    END IF;

    WHILE i <= p_count
        DO
            INSERT INTO `benefit_partner` (benefit_id,
                                           partner_id,
                                           is_deleted,
                                           created_at)
            VALUES (((i - 1) % benefit_cnt) + 1,
                    ((i - 1) % partner_cnt) + 1,
                    FALSE,
                    NOW());
            SET i = i + 1;
        END WHILE;
END$$

/*
 * 9) user_card (참조: user, card)
 */
DROP PROCEDURE IF EXISTS seed_user_card$$
CREATE PROCEDURE seed_user_card(IN p_count INT)
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE user_cnt INT;
    DECLARE card_cnt INT;
    SELECT COUNT(*) INTO user_cnt FROM `user`;
    SELECT COUNT(*) INTO card_cnt FROM `card`;
    IF user_cnt = 0 OR card_cnt = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Need user and card rows. Run seed_user and seed_card first.';
    END IF;

    WHILE i <= p_count
        DO
            INSERT INTO `user_card` (user_id,
                                     card_id,
                                     last3,
                                     is_deleted,
                                     is_deactivate,
                                     created_at)
            VALUES (((i - 1) % user_cnt) + 1,
                    ((i - 1) % card_cnt) + 1,
                    LPAD(CONVERT(((i - 1) % 1000), CHAR), 3, '0'),
                    FALSE,
                    FALSE,
                    NOW());
            SET i = i + 1;
        END WHILE;
END$$

/*
 * 10) user_card_transaction (참조: user_card, partner)
 */
DROP PROCEDURE IF EXISTS seed_user_card_transaction$$
CREATE PROCEDURE seed_user_card_transaction(IN p_count INT)
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE user_card_cnt INT;
    DECLARE partner_cnt INT;
    SELECT COUNT(*) INTO user_card_cnt FROM `user_card`;
    SELECT COUNT(*) INTO partner_cnt FROM `partner`;
    IF user_card_cnt = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Need user_card rows. Run seed_user_card first.';
    END IF;

    WHILE i <= p_count
        DO
            INSERT INTO `user_card_transaction` (user_card_id,
                                                 partner_id,
                                                 res_used_date,
                                                 res_used_time,
                                                 res_card_name,
                                                 res_used_amount,
                                                 res_payment_type,
                                                 is_deleted,
                                                 created_at)
            VALUES (((i - 1) % user_card_cnt) + 1,
                    IF(partner_cnt = 0, NULL, ((i - 1) % partner_cnt) + 1),
                    DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL ((i - 1) % 365) DAY), '%Y-%m-%d'),
                    CONCAT(LPAD(((i - 1) % 23), 2, '0'), ':', LPAD(((i - 1) % 59), 2, '0')),
                    CONCAT('CardName_', ((i - 1) % 1000) + 1),
                    LPAD(((i * 123) % 100000), 1, '1'),
                    'CARD',
                    FALSE,
                    NOW());
            SET i = i + 1;
        END WHILE;
END$$

DELIMITER ;
