package com.payper.codef.dto.response;

import com.payper.codef.dto.output.CardInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyCardListResponse {

    private List<CardInfo> data;

}