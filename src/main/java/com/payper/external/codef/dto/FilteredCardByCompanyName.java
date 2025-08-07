package com.payper.external.codef.dto;

import lombok.Data;

/**
 * CODEF에서 내 카드를 불러온 후에
 * 해당 카드를 DB에 등록된 카드와 매칭 시키는 과정에서 사용되는 정보들입니다.
 * 기본으로 일단 card 테이블의 card_id, card_name, card_type
 */
@Data
public class FilteredCardByCompanyName {
    private Long id;
    private String name;  // DB 저장 카드 이름
    private String type; // 'CREDIT', 'CHECK'
}
