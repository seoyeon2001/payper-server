START TRANSACTION;
-- 카드 등록
INSERT INTO card (company_name, card_name, card_type, card_image_url, card_issue_url)
VALUES ('KB국민카드', '노리2 체크카드(KB Pay)', 'CHECK', 'https://example.com/image/nori2.jpg', 'https://kbcard.com/issue/nori2');

-- 카드 ID 저장
SET @card_id = LAST_INSERT_ID();

-- 혜택 등록 (편의점)
INSERT INTO benefit (card_id, benefit_title, benefit_summary, benefit_description, benefit_icon_url)
VALUES (@card_id, '편의점', '[일상] GS25, CU 5% 할인', '[일상] GS25, CU 5% 할인
- 전월 이용실적 20만원 이상 시 제공
- 건당 이용조건 없음
- 월 할인한도 : 2천원 (월 4만원 이용금액까지 할인)
- 할인제외: 상품권/선불카드(선불전자지급수단 포함) 구입 및 충전 금액', 'https://example.com/icon/cvs.png');
SET @benefit_id = LAST_INSERT_ID();

-- 카테고리 등록
INSERT INTO category (category_name) VALUES ('모든 가맹점');
SET @pre_category_id1 = LAST_INSERT_ID();

INSERT INTO category (category_name, pre_category_id) VALUES
    ('편의점', @pre_category_id1);
SET @pre_category_id2 = LAST_INSERT_ID();

INSERT INTO category (category_name, pre_category_id) VALUES
    ('GS25', @pre_category_id2);
SET @category_id1 = LAST_INSERT_ID();

INSERT INTO category (category_name, pre_category_id) VALUES
    ('CU', @pre_category_id2);
SET @category_id2 = LAST_INSERT_ID();

-- 혜택-카테고리 매핑
INSERT INTO benefit_category (benefit_id, category_id)
VALUES (@benefit_id, @category_id1), (@benefit_id, @category_id2);

-- 실적 등급 등록
INSERT INTO grade (card_id, start, end, total_discount)
VALUES (@card_id, 200000, NULL, NULL);
SET @grade_id1 = LAST_INSERT_ID();

-- 할인 등록
INSERT INTO benefit_grade_discount (grade_id, benefit_id, type, amount, limit_count, limit_amount)
VALUES (@grade_id1, @benefit_id, 'RATE', 5, NULL, 2000);

-- 혜택 등록 (카페)
INSERT INTO benefit (card_id, benefit_title, benefit_summary, benefit_description, benefit_icon_url)
VALUES (@card_id, '카페', '[일상] 스타벅스, 커피빈 10% 할인', '[일상] 스타벅스, 커피빈 10% 할인
- 전월 이용실적 조건 없음
- 건당 이용조건 없음
- 월 할인한도 : 3천원 (월 3만원 이용금액까지 할인)
- 스타벅스는 사이렌오더 결제 포함
- 할인제외 : 상품권/선불카드(선불전자지급수단 포함) 구입 및 충전, 백화점, 대형마트, 철도·역사, 호텔 등에 입점한 가맹점 등', 'https://example.com/icon/cafe.png');
SET @benefit_id = LAST_INSERT_ID();

-- 카테고리 등록
INSERT INTO category (category_name, pre_category_id) VALUES ('카페', @pre_category_id1);
SET @pre_category_id2 = LAST_INSERT_ID();
INSERT INTO category (category_name, pre_category_id) VALUES ('스타벅스', @pre_category_id2);
SET @category_id1 = LAST_INSERT_ID();
INSERT INTO category (category_name, pre_category_id) VALUES ('커피빈', @pre_category_id2);
SET @category_id2 = LAST_INSERT_ID();

-- 혜택-카테고리 매핑
INSERT INTO benefit_category (benefit_id, category_id)
VALUES (@benefit_id, @category_id1), (@benefit_id, @category_id2);

-- 실적 등급 등록
INSERT INTO grade (card_id, start, end, total_discount)
VALUES (@card_id, 0, NULL, NULL);
SET @grade_id2 = LAST_INSERT_ID();

-- 할인 등록
INSERT INTO benefit_grade_discount (grade_id, benefit_id, type, amount, limit_count, limit_amount)
VALUES (@grade_id2, @benefit_id, 'RATE', 10, NULL, 3000);



-- 혜택 등록 (게임)
INSERT INTO benefit (card_id, benefit_title, benefit_summary, benefit_description, benefit_icon_url)
VALUES (@card_id, '게임', '[일상] 구글플레이스토어, 앱스토어 10% 할인', '[일상] 구글플레이스토어, 앱스토어 10% 할인
- 전월 이용실적 20만원 이상 시 제공
- 건당 이용조건 없음
- 월 할인한도 : 5천원 (월 5만원 이용금액까지 할인)
- 가맹점명이 "구글플레이스토어", "앱스토어"로 등록된 KB국민카드 가맹점에 한하여 제공', 'https://example.com/icon/cafe.png');
SET @benefit_id = LAST_INSERT_ID();

-- 카테고리 등록
INSERT INTO category (category_name, pre_category_id) VALUES ('게임', @pre_category_id1);
SET @pre_category_id2 = LAST_INSERT_ID();
INSERT INTO category (category_name, pre_category_id) VALUES ('구글플레이스토어', @pre_category_id2);
SET @category_id1 = LAST_INSERT_ID();
INSERT INTO category (category_name, pre_category_id) VALUES ('앱스토어', @pre_category_id2);
SET @category_id2 = LAST_INSERT_ID();

-- 혜택-카테고리 매핑
INSERT INTO benefit_category (benefit_id, category_id)
VALUES (@benefit_id, @category_id1), (@benefit_id, @category_id2);

-- 할인 등록
INSERT INTO benefit_grade_discount (grade_id, benefit_id, type, amount, limit_count, limit_amount)
VALUES (@grade_id1, @benefit_id, 'RATE', 10, NULL, 5000);



-- 혜택 등록 (공연/전시)
INSERT INTO benefit (card_id, benefit_title, benefit_summary, benefit_description, benefit_icon_url)
VALUES (@card_id, '공연/전시', '[일상] 인터파크 티켓 10% 할인', '[일상] 인터파크 티켓 10% 할인
- 전월 이용실적 20만원 이상 시 제공
- 건당 이용조건 없음
- 월 할인한도 : 7천원 (월 7만원 이용금액까지 할인)
- 공연 티켓 예매 시에만 제공', 'https://example.com/icon/cafe.png');
SET @benefit_id = LAST_INSERT_ID();

-- 카테고리 등록
INSERT INTO category (category_name, pre_category_id) VALUES ('공연/전시', @pre_category_id1);
SET @pre_category_id2 = LAST_INSERT_ID();
INSERT INTO category (category_name, pre_category_id) VALUES ('인터파크 티켓', @pre_category_id2);
SET @category_id1 = LAST_INSERT_ID();

-- 혜택-카테고리 매핑
INSERT INTO benefit_category (benefit_id, category_id)
VALUES (@benefit_id, @category_id1);

-- 할인 등록
INSERT INTO benefit_grade_discount (grade_id, benefit_id, type, amount, limit_count, limit_amount)
VALUES (@grade_id1, @benefit_id, 'RATE', 10, NULL, 7000);




-- 혜택 등록 (드럭스토어)
INSERT INTO benefit (card_id, benefit_title, benefit_summary, benefit_description, benefit_icon_url)
VALUES (@card_id, '드럭스토어', '[일상] 올리브영, 미용실 업종 5% 할인', '[일상] 올리브영, 미용실 업종 5% 할인
- 전월 이용실적 20만원 이상 시 제공
- 건당 이용조건 없음
- 월 할인한도 : 2천원 (월 4만원 이용금액까지 할인)
- KB국민카드 업종 분류 기준에 따라 지정 업종에 한하여 적용', 'https://example.com/icon/cafe.png');
SET @benefit_id = LAST_INSERT_ID();

-- 카테고리 등록
INSERT INTO category (category_name, pre_category_id) VALUES ('드럭스토어', @pre_category_id1);
SET @pre_category_id2 = LAST_INSERT_ID();
INSERT INTO category (category_name, pre_category_id) VALUES ('올리브영', @pre_category_id2);
SET @category_id1 = LAST_INSERT_ID();
INSERT INTO category (category_name, pre_category_id) VALUES ('미용실', @pre_category_id2);
SET @category_id2 = LAST_INSERT_ID();

-- 혜택-카테고리 매핑
INSERT INTO benefit_category (benefit_id, category_id)
VALUES (@benefit_id, @category_id1), (@benefit_id, @category_id2);

-- 할인 등록
INSERT INTO benefit_grade_discount (grade_id, benefit_id, type, amount, limit_count, limit_amount)
VALUES (@grade_id1, @benefit_id, 'RATE', 5, NULL, 2000);



-- 혜택 등록 (디지털구독)
INSERT INTO benefit (card_id, benefit_title, benefit_summary, benefit_description, benefit_icon_url)
VALUES (@card_id, '디지털구독', '[일상]넷플릭스, 유튜브프리미엄 1,000원 할인', '[일상]넷플릭스, 유튜브프리미엄 1,000원 할인
- 전월 이용실적 20만원 이상 시 제공
- 건당 1만원 이상 이용 시 제공
- 월 할인횟수: 2회 (월 할인한도 2천원 이내 제공)', 'https://example.com/icon/cafe.png');
SET @benefit_id = LAST_INSERT_ID();

-- 카테고리 등록
INSERT INTO category (category_name, pre_category_id) VALUES ('디지털구독', @pre_category_id1);
SET @pre_category_id2 = LAST_INSERT_ID();
INSERT INTO category (category_name, pre_category_id) VALUES ('넷플릭스', @pre_category_id2);
SET @category_id1 = LAST_INSERT_ID();
INSERT INTO category (category_name, pre_category_id) VALUES ('유튜브프리미엄', @pre_category_id2);
SET @category_id2 = LAST_INSERT_ID();

-- 혜택-카테고리 매핑
INSERT INTO benefit_category (benefit_id, category_id)
VALUES (@benefit_id, @category_id1), (@benefit_id, @category_id2);

-- 할인 등록
INSERT INTO benefit_grade_discount (grade_id, benefit_id, type, amount, limit_count, limit_amount, min_payment)
VALUES (@grade_id1, @benefit_id, 'FIXED_AMOUNT', 1000, 2, NULL, 10000);


-- 배달앱
INSERT INTO benefit (card_id, benefit_title, benefit_summary, benefit_description, benefit_icon_url)
VALUES (@card_id, '배달앱', '[일상] 배달의 민족, 요기요 1,000원 할인', '[일상] 배달의 민족, 요기요 1,000원 할인
- 전월 이용실적 20만원 이상 시 제공
- 건당 1만원 이상 이용 시 제공
- 월 할인횟수: 1회 (월 할인한도 1천원 이내 제공)
- 본인회원 기준으로 월간 통합할인한도 내에서 혜택이 제공됩니다.', 'https://example.com/icon/delivery.png');
SET @benefit_id = LAST_INSERT_ID();

INSERT INTO category (category_name, pre_category_id) VALUES ('배달앱', @pre_category_id1);
SET @pre_category_id2 = LAST_INSERT_ID();
INSERT INTO category (category_name, pre_category_id) VALUES ('배달의 민족', @pre_category_id2);
SET @category_id1 = LAST_INSERT_ID();
INSERT INTO category (category_name, pre_category_id) VALUES ('요기요', @pre_category_id2);
SET @category_id2 = LAST_INSERT_ID();

INSERT INTO benefit_category (benefit_id, category_id)
VALUES (@benefit_id, @category_id1), (@benefit_id, @category_id2);

INSERT INTO benefit_grade_discount (grade_id, benefit_id, type, amount, limit_count, limit_amount, min_payment)
VALUES (@grade_id1, @benefit_id, 'FIXED_AMOUNT', 1000, 1, NULL, 10000);


-- 통신
INSERT INTO benefit (card_id, benefit_title, benefit_summary, benefit_description, benefit_icon_url)
VALUES (@card_id, '통신', '[일상] SKT, KT, LG U+, Liiv M 2,500원 할인', '[일상] SKT, KT, LG U+, Liiv M 2,500원 할인
- 전월 이용실적 20만원 이상 시 제공
- 통신요금 5만원 이상 이용 시 제공
- 이동통신요금 자동이체 납부금액 기준 제공
- 월 할인횟수: 1회 (월 할인한도 2천5백원 이내 제공)
- 본인회원 기준으로 월간 통합할인한도 내에서 혜택이 제공됩니다.', 'https://example.com/icon/telecom.png');
SET @benefit_id = LAST_INSERT_ID();

INSERT INTO category (category_name, pre_category_id) VALUES ('통신요금', @pre_category_id1);
SET @pre_category_id2 = LAST_INSERT_ID();
INSERT INTO category (category_name, pre_category_id) VALUES ('SKT', @pre_category_id2), ('KT', @pre_category_id2), ('LG U+', @pre_category_id2), ('Liiv M', @pre_category_id2);

-- 각 category_id 순차 저장 생략 가능
INSERT INTO benefit_category (benefit_id, category_id)
SELECT @benefit_id, category_id FROM category WHERE category_name IN ('SKT', 'KT', 'LG U+', 'Liiv M');

INSERT INTO benefit_grade_discount (grade_id, benefit_id, type, amount, limit_count, limit_amount, min_payment)
VALUES (@grade_id1, @benefit_id, 'FIXED_AMOUNT', 2500, 1, NULL, 50000);


-- 영화관
INSERT INTO benefit (card_id, benefit_title, benefit_summary, benefit_description, benefit_icon_url)
VALUES (@card_id, '영화', '[일상] CGV 4,000원 할인', '[일상] CGV 4,000원 할인
- 전월 이용실적 20만원 이상 시 제공
- 건당 1만원 이상 이용 시 제공
- 현장 결제, 공식 홈페이지 및 모바일(앱) 결제 시 제공
- 할인 제외: 상품권 구매 및 매점 이용금액
- 월 할인횟수: 2회 (월 할인한도 8천원 이내 제공)
- 본인회원 기준으로 월간 통합할인한도 내에서 혜택이 제공됩니다.', 'https://example.com/icon/movie.png');
SET @benefit_id = LAST_INSERT_ID();

INSERT INTO category (category_name, pre_category_id) VALUES ('영화', @pre_category_id1);
SET @pre_category_id2 = LAST_INSERT_ID();
INSERT INTO category (category_name, pre_category_id) VALUES ('CGV', @pre_category_id2);
SET @category_id1 = LAST_INSERT_ID();

INSERT INTO benefit_category (benefit_id, category_id) VALUES (@benefit_id, @category_id1);

INSERT INTO benefit_grade_discount (grade_id, benefit_id, type, amount, limit_count, limit_amount, min_payment)
VALUES (@grade_id1, @benefit_id, 'FIXED_AMOUNT', 4000, 2, NULL, 10000);


-- 테마파크
INSERT INTO benefit (card_id, benefit_title, benefit_summary, benefit_description, benefit_icon_url)
VALUES (@card_id, '테마파크', '[일상] 에버랜드, 롯데월드 15,000원 할인', '[일상] 에버랜드, 롯데월드 15,000원 할인
- 전월 이용실적 20만원 이상 시 제공
- 건당 3만원 이상 이용 시 제공
- 티켓 결제 시 제공
- 할인 제외: 상품권 구매 및 매점 이용금액
- 월 할인횟수: 1회 (월 할인한도 1만5천원 이내 제공)
- 본인회원 기준으로 월간 통합할인한도 내에서 혜택이 제공됩니다.', 'https://example.com/icon/themepark.png');
SET @benefit_id = LAST_INSERT_ID();

INSERT INTO category (category_name, pre_category_id) VALUES ('테마파크', @pre_category_id1);
SET @pre_category_id2 = LAST_INSERT_ID();
INSERT INTO category (category_name, pre_category_id) VALUES ('에버랜드', @pre_category_id2), ('롯데월드', @pre_category_id2);

INSERT INTO benefit_category (benefit_id, category_id)
SELECT @benefit_id, category_id FROM category WHERE category_name IN ('에버랜드', '롯데월드');

INSERT INTO benefit_grade_discount (grade_id, benefit_id, type, amount, limit_count, limit_amount, min_payment)
VALUES (@grade_id1, @benefit_id, 'FIXED_AMOUNT', 15000, 1, NULL, 30000);

-- KB Pay 오프라인
INSERT INTO benefit (card_id, benefit_title, benefit_summary, benefit_description, benefit_icon_url)
VALUES (@card_id, 'KB Pay 오프라인', '[KB Pay] 오프라인 가맹점 2% 추가 할인', '[KB Pay] 오프라인 가맹점 2% 추가 할인
- 전월 이용실적 30만원 이상 시 제공
- 건당 이용조건 없음
- 월 할인한도: 3천원(월15만원 이용금액까지 할인)
- 일상 혜택과 중복적용 가능
- 본인회원 기준으로 월간 통합할인한도 내에서 혜택이 제공됩니다.', 'https://example.com/icon/kbpay_offline.png');
SET @benefit_id = LAST_INSERT_ID();

INSERT INTO category (category_name, pre_category_id) VALUES ('오프라인 가맹점', @pre_category_id1);
SET @category_id1 = LAST_INSERT_ID();

INSERT INTO benefit_category (benefit_id, category_id) VALUES (@benefit_id, @category_id1);

INSERT INTO grade (card_id, start, end, total_discount) VALUES (@card_id, 300000, NULL, NULL);
SET @grade_id3 = LAST_INSERT_ID();

INSERT INTO benefit_grade_discount (grade_id, benefit_id, type, amount, limit_count, limit_amount)
VALUES (@grade_id3, @benefit_id, 'RATE', 2, NULL, 3000);


-- KB PAY 온라인 2% 할인
INSERT INTO benefit (card_id, benefit_title, benefit_summary, benefit_description, benefit_icon_url)
VALUES (@card_id, 'KB Pay 온라인', '[KB Pay] 온라인 가맹점 2% 추가 할인', '[KB Pay] 온라인 가맹점 2% 추가 할인
- 전월 이용실적 30만원 이상 시 제공
- 건당 이용조건 없음
- 월 할인한도: 2천원(월15만원 이용금액까지 할인)
- 일상 혜택과 중복적용 가능
- 본인회원 기준으로 월간 통합할인한도 내에서 혜택이 제공됩니다.', 'https://example.com/icon/kbpay_online.png');
SET @benefit_id = LAST_INSERT_ID();

INSERT INTO category (category_name, pre_category_id) VALUES ('온라인 가맹점', @pre_category_id1);
SET @category_id1 = LAST_INSERT_ID();

INSERT INTO benefit_category (benefit_id, category_id) VALUES (@benefit_id, @category_id1);

INSERT INTO benefit_grade_discount (grade_id, benefit_id, type, amount, limit_count, limit_amount)
VALUES (@grade_id3, @benefit_id, 'RATE', 2, NULL, 2000);
COMMIT;