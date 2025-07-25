package com.payper.benefit.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grade {
    private Long gradeId;

    private Long cardId;

    private Integer start;

    private Integer end;

    private Integer totalDiscount;
}