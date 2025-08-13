package com.payper.domain.benefit;

import com.payper.domain.benefit.domain.Benefit;
import com.payper.domain.benefit.dto.request.UpdateBenefitRequest;
import com.payper.domain.benefit.dto.response.BenefitResponse;
import com.payper.domain.benefit.dto.request.CreateBenefitRequest;
import com.payper.domain.card.domain.Card;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BenefitMapper {
    List<BenefitResponse> findAllByCardId(int cardId);

    void save(@Param("cardId") int cardId, @Param("request") CreateBenefitRequest request);

    int update(@Param("benefitId") int benefitId, @Param("request") UpdateBenefitRequest request);

    int delete(@Param("cardId") Integer cardId, @Param("benefitId") Integer benefitId);
}
