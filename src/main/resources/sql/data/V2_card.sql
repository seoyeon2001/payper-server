START TRANSACTION;

-- 카드사 등록
INSERT INTO card_company (company_name)
VALUES ('KB국민카드');
SET @company_id = LAST_INSERT_ID();

-- 카드 등록
INSERT INTO card (card_name, card_type, card_image_url, card_issue_url, company_id)
VALUES (
           '노리2 체크카드(KB Pay)',
           'CHECK',
           'https://example.com/image/nori2.jpg',
           'https://kbcard.com/issue/nori2',
           @company_id
       );
SET @card_id = LAST_INSERT_ID();

-- 공통 카테고리 등록 및 변수 설정
INSERT INTO category (category_name) VALUES ('편의점'); SET @편의점 = LAST_INSERT_ID();
INSERT INTO category (category_name) VALUES ('드럭스토어'); SET @드럭 = LAST_INSERT_ID();
INSERT INTO category (category_name) VALUES ('디지털 구독'); SET @디지털 = LAST_INSERT_ID();
INSERT INTO category (category_name) VALUES ('배달앱'); SET @배달 = LAST_INSERT_ID();
INSERT INTO category (category_name) VALUES ('통신요금'); SET @통신 = LAST_INSERT_ID();
INSERT INTO category (category_name) VALUES ('영화'); SET @영화 = LAST_INSERT_ID();
INSERT INTO category (category_name) VALUES ('테마파크'); SET @테마 = LAST_INSERT_ID();
INSERT INTO category (category_name) VALUES ('KB Pay 오프라인'); SET @오프 = LAST_INSERT_ID();
INSERT INTO category (category_name) VALUES ('KB Pay 온라인'); SET @온라 = LAST_INSERT_ID();

-- 공통 등급 등록
INSERT INTO grade (card_id, start, total_discount) VALUES (@card_id, 200000, NULL); SET @grade_id1 = LAST_INSERT_ID();
INSERT INTO grade (card_id, start, total_discount) VALUES (@card_id, 300000, NULL); SET @grade_id3 = LAST_INSERT_ID();

-- ===== 편의점 =====
INSERT INTO partner (category_id, partner_name) VALUES (@편의점, 'GS25');
SET @partner_gs25 = LAST_INSERT_ID();

INSERT INTO partner (category_id, partner_name) VALUES (@편의점, 'CU');
SET @partner_cu = LAST_INSERT_ID();

INSERT INTO benefit (card_id, benefit_title, benefit_summary, benefit_description, benefit_icon_url)
VALUES (
           @card_id, '편의점', '[일상] GS25, CU 5% 할인',
           '[일상] GS25, CU 5% 할인\n- 전월 이용실적 20만원 이상 시 제공\n- 건당 이용조건 없음\n- 월 할인한도 : 2천원\n- 할인제외: 상품권/선불카드 구입 및 충전 금액',
           'https://example.com/icon/cvs.png'
       ); SET @benefit_id = LAST_INSERT_ID();
INSERT INTO benefit_category (benefit_id, category_id) VALUES (@benefit_id, @편의점);
INSERT INTO benefit_partner (benefit_id, partner_id) VALUES
                                                         (@benefit_id, @partner_gs25), (@benefit_id, @partner_cu);

INSERT INTO benefit_grade_discount (grade_id, benefit_id, type, amount, limit_amount, min_payment)
VALUES (@grade_id1, @benefit_id, 'RATE', 5, 2000, 0);

-- ===== 드럭스토어 =====
INSERT INTO partner (category_id, partner_name) VALUES
                                                    (@드럭, '올리브영');
SET @partner_olive = LAST_INSERT_ID();
INSERT INTO partner (category_id, partner_name) VALUES
                                                    (@드럭, '미용실');
SET @partner_hair = LAST_INSERT_ID();

INSERT INTO benefit (card_id, benefit_title, benefit_summary, benefit_description, benefit_icon_url)
VALUES (
           @card_id, '드럭스토어', '[일상] 올리브영 10% 청구할인',
           '[일상] 올리브영 10% 청구할인\n- 전월 실적 20만원 이상\n- 월 할인한도 5천원',
           'https://example.com/icon/drugstore.png'
       ); SET @benefit_id = LAST_INSERT_ID();

INSERT INTO benefit_category (benefit_id, category_id) VALUES (@benefit_id, @드럭);
INSERT INTO benefit_partner (benefit_id, partner_id) VALUES
                                                         (@benefit_id, @partner_olive), (@benefit_id, @partner_hair);

INSERT INTO benefit_grade_discount (grade_id, benefit_id, type, amount, limit_amount)
VALUES (@grade_id1, @benefit_id, 'RATE', 10, 5000);

-- ===== 디지털 구독 =====
INSERT INTO partner (category_id, partner_name) VALUES (@디지털, '넷플릭스'); SET @partner_netflix = LAST_INSERT_ID();
INSERT INTO partner (category_id, partner_name) VALUES (@디지털, '유튜브프리미엄'); SET @partner_youtube = LAST_INSERT_ID();

INSERT INTO benefit (card_id, benefit_title, benefit_summary, benefit_description, benefit_icon_url)
VALUES (
           @card_id, '디지털 구독', '[디지털] 넷플릭스, 유튜브프리미엄 20% 청구할인',
           '[디지털] 넷플릭스, 유튜브프리미엄 20% 청구할인\n- 전월 실적 30만원 이상\n- 월 할인한도 4천원',
           'https://example.com/icon/digital.png'
       ); SET @benefit_id = LAST_INSERT_ID();

INSERT INTO benefit_category (benefit_id, category_id) VALUES (@benefit_id, @디지털);
INSERT INTO benefit_partner (benefit_id, partner_id) VALUES
                                                         (@benefit_id, @partner_netflix), (@benefit_id, @partner_youtube);
INSERT INTO benefit_grade_discount (grade_id, benefit_id, type, amount, limit_amount)
VALUES (@grade_id3, @benefit_id, 'RATE', 20, 4000);

-- ===== 배달앱 =====
INSERT INTO partner (category_id, partner_name) VALUES (@배달, '배달의 민족'); SET @partner_baemin = LAST_INSERT_ID();
INSERT INTO partner (category_id, partner_name) VALUES (@배달, '요기요'); SET @partner_yogiyo = LAST_INSERT_ID();

INSERT INTO benefit (card_id, benefit_title, benefit_summary, benefit_description, benefit_icon_url)
VALUES (
           @card_id, '배달앱', '[일상] 배달의 민족, 요기요 10% 청구할인',
           '[일상] 배달의 민족, 요기요 10% 청구할인\n- 전월 실적 20만원 이상\n- 월 할인한도 3천원',
           'https://example.com/icon/delivery.png'
       ); SET @benefit_id = LAST_INSERT_ID();

INSERT INTO benefit_category (benefit_id, category_id) VALUES (@benefit_id, @배달);
INSERT INTO benefit_partner (benefit_id, partner_id) VALUES
                                                         (@benefit_id, @partner_baemin), (@benefit_id, @partner_yogiyo);
INSERT INTO benefit_grade_discount (grade_id, benefit_id, type, amount, limit_amount)
VALUES (@grade_id1, @benefit_id, 'RATE', 10, 3000);

-- ===== 통신요금 =====
INSERT INTO partner (category_id, partner_name) VALUES (@통신, 'SKT'); SET @partner_skt = LAST_INSERT_ID();
INSERT INTO partner (category_id, partner_name) VALUES (@통신, 'KT'); SET @partner_kt = LAST_INSERT_ID();
INSERT INTO partner (category_id, partner_name) VALUES (@통신, 'LG U+'); SET @partner_lgu = LAST_INSERT_ID();
INSERT INTO partner (category_id, partner_name) VALUES (@통신, 'Liiv M'); SET @partner_liiv = LAST_INSERT_ID();

INSERT INTO benefit (card_id, benefit_title, benefit_summary, benefit_description, benefit_icon_url)
VALUES (
           @card_id, '통신요금', '[고정비] SKT, KT, LG U+, Liiv M 5천원 청구할인',
           '[고정비] SKT, KT, LG U+, Liiv M 5천원 청구할인\n- 전월 실적 30만원 이상\n- 할인 제외 대상 없음',
           'https://example.com/icon/telecom.png'
       ); SET @benefit_id = LAST_INSERT_ID();

INSERT INTO benefit_category (benefit_id, category_id) VALUES (@benefit_id, @통신);
INSERT INTO benefit_partner (benefit_id, partner_id) VALUES
                                                         (@benefit_id, @partner_skt),
                                                         (@benefit_id, @partner_kt),
                                                         (@benefit_id, @partner_lgu),
                                                         (@benefit_id, @partner_liiv);
INSERT INTO benefit_grade_discount (grade_id, benefit_id, type, amount)
VALUES (@grade_id3, @benefit_id, 'FIXED_AMOUNT', 5000);

-- ===== 영화 =====
INSERT INTO partner (category_id, partner_name) VALUES (@영화, 'CGV'); SET @partner_cgv = LAST_INSERT_ID();

INSERT INTO benefit (card_id, benefit_title, benefit_summary, benefit_description, benefit_icon_url)
VALUES (
           @card_id, '영화', '[여가] CGV 3천원 청구할인',
           '[여가] CGV 3천원 청구할인\n- 전월 실적 20만원 이상\n- 건당 이용금액 1만원 이상\n- 월 1회 제공',
           'https://example.com/icon/movie.png'
       ); SET @benefit_id = LAST_INSERT_ID();

INSERT INTO benefit_category (benefit_id, category_id) VALUES (@benefit_id, @영화);
INSERT INTO benefit_partner (benefit_id, partner_id) VALUES (@benefit_id, @partner_cgv);
INSERT INTO benefit_grade_discount (grade_id, benefit_id, type, amount, limit_count, min_payment)
VALUES (@grade_id1, @benefit_id, 'FIXED_AMOUNT', 3000, 1, 10000);

-- ===== 테마파크 =====
INSERT INTO partner (category_id, partner_name) VALUES (@테마, '에버랜드'); SET @partner_ever = LAST_INSERT_ID();
INSERT INTO partner (category_id, partner_name) VALUES (@테마, '롯데월드'); SET @partner_lotte = LAST_INSERT_ID();

INSERT INTO benefit (card_id, benefit_title, benefit_summary, benefit_description, benefit_icon_url)
VALUES (
           @card_id, '테마파크', '[여가] 에버랜드, 롯데월드 자유이용권 본인 50% 할인',
           '[여가] 에버랜드, 롯데월드 자유이용권 본인 50% 할인\n- 전월 실적 30만원 이상\n- 월 1회 제공',
           'https://example.com/icon/themepark.png'
       ); SET @benefit_id = LAST_INSERT_ID();

INSERT INTO benefit_category (benefit_id, category_id) VALUES (@benefit_id, @테마);
INSERT INTO benefit_partner (benefit_id, partner_id) VALUES
                                                         (@benefit_id, @partner_ever), (@benefit_id, @partner_lotte);
INSERT INTO benefit_grade_discount (grade_id, benefit_id, type, amount, limit_count)
VALUES (@grade_id3, @benefit_id, 'RATE', 50, 1);

-- ===== KB Pay 오프라인 =====
INSERT INTO partner (category_id, partner_name) VALUES (@오프, 'KB Pay 오프라인'); SET @partner_kbpay_off = LAST_INSERT_ID();

INSERT INTO benefit (card_id, benefit_title, benefit_summary, benefit_description, benefit_icon_url)
VALUES (
           @card_id, 'KB Pay 오프라인', '[페이] 오프라인 가맹점 5% 청구할인',
           '[페이] KB Pay 오프라인 결제 시 5% 청구할인\n- 전월 실적 20만원 이상\n- 월 할인한도 2천원',
           'https://example.com/icon/kbpay_off.png'
       ); SET @benefit_id = LAST_INSERT_ID();

INSERT INTO benefit_category (benefit_id, category_id) VALUES (@benefit_id, @오프);
INSERT INTO benefit_partner (benefit_id, partner_id) VALUES (@benefit_id, @partner_kbpay_off);
INSERT INTO benefit_grade_discount (grade_id, benefit_id, type, amount, limit_amount)
VALUES (@grade_id1, @benefit_id, 'RATE', 5, 2000);

-- ===== KB Pay 온라인 =====
INSERT INTO partner (category_id, partner_name) VALUES (@온라, 'KB Pay 온라인'); SET @partner_kbpay_on = LAST_INSERT_ID();

INSERT INTO benefit (card_id, benefit_title, benefit_summary, benefit_description, benefit_icon_url)
VALUES (
           @card_id, 'KB Pay 온라인', '[페이] 온라인 가맹점 5% 청구할인',
           '[페이] KB Pay 온라인 결제 시 5% 청구할인\n- 전월 실적 20만원 이상\n- 월 할인한도 2천원',
           'https://example.com/icon/kbpay_on.png'
       ); SET @benefit_id = LAST_INSERT_ID();

INSERT INTO benefit_category (benefit_id, category_id) VALUES (@benefit_id, @온라);
INSERT INTO benefit_partner (benefit_id, partner_id) VALUES (@benefit_id, @partner_kbpay_on);
INSERT INTO benefit_grade_discount (grade_id, benefit_id, type, amount, limit_amount)
VALUES (@grade_id1, @benefit_id, 'RATE', 5, 2000);


COMMIT;
