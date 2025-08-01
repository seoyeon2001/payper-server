package com.payper.domain.partner;

import com.payper.domain.partner.dto.PartnerIdNameDto;
import com.payper.domain.partner.dto.SearchPartnersResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PartnerMapper {
    List<PartnerIdNameDto> findAllByCategoryId(int categoryId);
    PartnerIdNameDto findByPartnerName(String partnerName);
    List<SearchPartnersResponse> searchWithConditions(
            @Param("name") String name,
            @Param("category") List<String> category
    );
}
