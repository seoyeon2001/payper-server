DELIMITER $$

CREATE PROCEDURE insert_dummy_users(IN total INT)
BEGIN
    DECLARE i INT DEFAULT 1;

    WHILE i <= total DO
            INSERT INTO `user` (
                username,
                password,
                nickname,
                connected_id,
                role,
                is_deleted
            ) VALUES (
                         -- username: user1, user2, ...
                         CONCAT('user', i),
                         -- password: password
                         '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
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
