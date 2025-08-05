package com.payper.domain.partner;

import com.payper.domain.partner.dto.PartnerDetailResponse;
import com.payper.domain.partner.dto.PartnerTempDto;
import com.payper.domain.partner.dto.SearchPartnersResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PartnerMapper {
    List<PartnerTempDto> findAllByCategoryId(Integer categoryId);
    PartnerTempDto findByPartnerName(String partnerName);
    List<SearchPartnersResponse> searchWithConditions(
            @Param("name") String name,
            @Param("category") List<String> category
    );
    PartnerTempDto findPartnerDetailById(Integer partnerId);
}
