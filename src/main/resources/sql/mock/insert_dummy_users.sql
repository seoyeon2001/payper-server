DELIMITER $$

CREATE PROCEDURE insert_dummy_users(IN total INT)
BEGIN
    DECLARE i INT DEFAULT 1;

    WHILE i <= total DO
            INSERT INTO `user` (
                oauth_provider,
                oauth_id,
                nickname,
                connected_id,
                role,
                is_deleted
            ) VALUES (
                         -- oauth_provider: google / kakao / facebook 중 랜덤
                         ELT(FLOOR(1 + RAND() * 3), 'google', 'kakao', 'facebook'),
                         -- oauth_id: 랜덤 숫자 조합
                         CONCAT('oauth_', FLOOR(RAND() * 1000000)),
                         -- nickname: User1, User2, ...
                         CONCAT('User', i),
                         -- connected_id: 50% 확률로 NULL
                         IF(RAND() > 0.5, CONCAT('conn_', FLOOR(RAND() * 100000)), NULL),
                         -- role: 90% ROLE_USER, 10% ROLE_ADMIN
                         IF(RAND() < 0.1, 'ROLE_ADMIN', 'ROLE_USER'),
                         -- is_deleted: 5% TRUE
                         IF(RAND() < 0.05, TRUE, FALSE)
                     );

            SET i = i + 1;
        END WHILE;
END$$

DELIMITER ;
