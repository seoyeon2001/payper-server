package com.payper.domain.benefit;

import com.payper.domain.benefit.dto.BenefitResponse;
import com.payper.domain.benefit.dto.CreateBenefitRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BenefitMapper {
    List<BenefitResponse> findAllByCardId(int cardId);

    void createBenefit(CreateBenefitRequest request);
}
