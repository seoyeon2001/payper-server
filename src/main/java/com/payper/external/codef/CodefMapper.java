package com.payper.external.codef;

import com.payper.external.codef.dto.FilteredCardByCompanyName;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CodefMapper {
    List<FilteredCardByCompanyName> findCardByCompanyName(String companyName);
}