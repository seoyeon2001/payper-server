# 아래 순서대로 실행되어야 함
# p_count: 삽입할 레코드 수
CALL seed_category(20);
CALL seed_card_company(20);
CALL seed_user(10000);
CALL seed_card(3000);
CALL seed_partner(1500);
CALL seed_benefit(20000);
CALL seed_benefit_category(20000);
CALL seed_benefit_partner(20000);
CALL seed_user_card(20000);
CALL seed_user_card_transaction(20000);
