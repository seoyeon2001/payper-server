START TRANSACTION;

-- 사용자 등록
INSERT INTO `user` (oauth_provider, oauth_id, nickname, connected_id)
VALUES ('kakao', '1234567890', '김은지', 'connect_abc123');
SET @user_id = LAST_INSERT_ID();

-- 사용자 카드
INSERT INTO `user_card` (user_id, card_id)
VALUES (@user_id, 1);

COMMIT;


INSERT INTO `user_card` (user_id, card_id)
VALUES (2, 1);