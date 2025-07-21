package com.payper.common.pagination;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE) //private all생성자로
public class PageRequest {
    private int page; //요청 페이지
    private int amount; // 한 페이지당 건수

    public PageRequest(){
        page = 1;
        amount = 10;
    }

    public static PageRequest of(int page, int amount){
        return new PageRequest(page, amount);
    }

    public int getOffset() {
        return (page - 1) * amount;
    }
}
