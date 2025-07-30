START TRANSACTION;

-- 1. 기존 카드 ID 조회
SELECT card_id INTO @base_card_id
FROM card
WHERE card_name = '노리2 체크카드(KB Pay)';

-- 2. 새 카드 등록
INSERT INTO card (company_name, card_name, card_type, card_image_url, card_issue_url, annual_fee)
SELECT company_name, 'KB NEW 체크카드', card_type, card_image_url, card_issue_url, annual_fee
FROM card WHERE card_id = @base_card_id;
SET @new_card_id = LAST_INSERT_ID();

-- 3. 기존 grade 복사 (ID 매핑 테이블을 위한 임시 테이블 생성)
CREATE TEMPORARY TABLE temp_grade_map (old_grade_id INT, new_grade_id INT);

INSERT INTO grade (card_id, start, end, total_discount)
SELECT @new_card_id, start, end, total_discount
FROM grade
WHERE card_id = @base_card_id;

-- 3-1. 새 grade ID 매핑 저장
INSERT INTO temp_grade_map (old_grade_id, new_grade_id)
SELECT g_old.grade_id, g_new.grade_id
FROM grade g_old
         JOIN grade g_new ON g_old.start = g_new.start AND g_old.end <=> g_new.end
WHERE g_old.card_id = @base_card_id AND g_new.card_id = @new_card_id;

-- 4. 기존 benefit 복사
CREATE TEMPORARY TABLE temp_benefit_map (old_benefit_id INT, new_benefit_id INT);

INSERT INTO benefit (card_id, benefit_title, benefit_summary, benefit_description, benefit_icon_url)
SELECT @new_card_id, benefit_title, benefit_summary, benefit_description, benefit_icon_url
FROM benefit WHERE card_id = @base_card_id;

-- 4-1. benefit 매핑 저장
INSERT INTO temp_benefit_map (old_benefit_id, new_benefit_id)
SELECT b_old.benefit_id, b_new.benefit_id
FROM benefit b_old
         JOIN benefit b_new ON b_old.benefit_title = b_new.benefit_title AND b_old.card_id = @base_card_id AND b_new.card_id = @new_card_id;

-- 5. benefit_category 복사
INSERT INTO benefit_category (benefit_id, category_id)
SELECT tb.new_benefit_id, bc.category_id
FROM benefit_category bc
         JOIN temp_benefit_map tb ON bc.benefit_id = tb.old_benefit_id;

-- 6. benefit_grade_discount 복사
INSERT INTO benefit_grade_discount (grade_id, benefit_id, type, amount, limit_count, limit_amount, min_payment)
SELECT tg.new_grade_id, tb.new_benefit_id, bgd.type, bgd.amount, bgd.limit_count, bgd.limit_amount, bgd.min_payment
FROM benefit_grade_discount bgd
         JOIN temp_benefit_map tb ON bgd.benefit_id = tb.old_benefit_id
         JOIN temp_grade_map tg ON bgd.grade_id = tg.old_grade_id;

-- 7. 임시 테이블 삭제
DROP TEMPORARY TABLE temp_benefit_map;
DROP TEMPORARY TABLE temp_grade_map;

COMMIT;
