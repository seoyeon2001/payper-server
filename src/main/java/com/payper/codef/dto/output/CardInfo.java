package com.payper.codef.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardInfo {
    private String resCardType;
    private String resValidPeriod;
    private String resCardName;
    private String resTrafficYN;
    private String resIssueDate;
    private String resUserNm;
    private String resSleepYN;
    private String resCardNo;
    private String resState;
    private String resImageLink;
}
