package com.payper.domain.benefit.dto.response;

import com.payper.domain.benefit.domain.BenefitGradeDiscount;
import com.payper.domain.benefit.domain.DiscountType;
import com.payper.global.exception.CustomIllegalArgumentException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BenefitGradeDiscountResponse {
	private Integer id;
	private GradeResponse gradeResponse;
	private DiscountType type;
	private Long amount;
	private Long limitCount;
	private Long limitAmount;
	private Long minPayment;

	public static BenefitGradeDiscountResponse toDTO(
			final BenefitGradeDiscount benefitGradeDiscount,
			final GradeResponse gradeResponse) {

		if(benefitGradeDiscount == null) {
			throw new CustomIllegalArgumentException("benefitGradeDiscount");
		}

		return BenefitGradeDiscountResponse.builder()
				.id(benefitGradeDiscount.getBenefitGradeDiscountId())
				.gradeResponse(gradeResponse)
				.type(benefitGradeDiscount.getType())
				.amount(benefitGradeDiscount.getAmount())
				.limitCount(benefitGradeDiscount.getLimitCount())
				.limitAmount(benefitGradeDiscount.getLimitAmount())
				.minPayment(benefitGradeDiscount.getMinPayment())
				.build();
	}
}