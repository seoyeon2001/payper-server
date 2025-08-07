package com.payper.domain.partner;

import com.payper.domain.partner.domain.Partner;
import com.payper.domain.partner.dto.PartnerTempDto;
import com.payper.domain.partner.dto.SearchPartnersResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PartnerMapper {
    List<PartnerTempDto> findAllByCategoryId(Integer categoryId);
    PartnerTempDto findByName(String partnerName);
    Integer findIdByName(String partnerName);
    List<SearchPartnersResponse> searchWithConditions(
            @Param("name") String name,
            @Param("category") List<String> category
    );
    PartnerTempDto findPartnerDetailById(Integer partnerId);
    void registerBenefitPartner(@Param("benefitId") Integer benefitId,
                                 @Param("partnerId") Integer partnerId);
    List<Partner> selectAll();
}
