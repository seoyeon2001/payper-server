package com.payper.partner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PartnerSearchOption {
    private final String query;
    private final int radius;
}