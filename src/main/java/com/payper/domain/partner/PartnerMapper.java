package com.payper.domain.partner;

import com.payper.domain.partner.domain.Partner;
import com.payper.domain.partner.dto.PartnerIdNameDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PartnerMapper {
    List<PartnerIdNameDto> findAllByCategoryId(int categoryId);
    PartnerIdNameDto findByPartnerName(String partnerName);
}
