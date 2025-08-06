package com.payper.domain.benefit;

import com.payper.domain.benefit.dto.request.UpdateBenefitRequest;
import com.payper.domain.benefit.dto.response.BenefitResponse;
import com.payper.domain.benefit.dto.request.CreateBenefitRequest;
import com.payper.external.crawling.dto.Discount;
import com.payper.external.crawling.dto.Grade;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BenefitMapper {
    List<BenefitResponse> findAllByCardId(int cardId);

    void createBenefit(@Param("cardId") int cardId, @Param("request") CreateBenefitRequest request);

    int updateBenefit(@Param("benefitId") int benefitId, @Param("request") UpdateBenefitRequest request);

    int softDeleteBenefit(@Param("cardId") Integer cardId, @Param("benefitId") Integer benefitId);

    void registerGrades(@Param("grades") List<Grade> grades, @Param("cardId") Integer cardId);

    Integer findGradeIdByCardIdAndStart(@Param("cardId") Integer cardId, @Param("start") Long start);

    void registerBenefitGradeDiscount(@Param("discount") Discount discount,
                                      @Param("benefitId") Integer benefitId,
                                      @Param("gradeId") Integer gradeId);
}
