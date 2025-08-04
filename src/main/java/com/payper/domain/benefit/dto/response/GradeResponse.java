package com.payper.domain.benefit.dto.response;

import com.payper.domain.benefit.domain.Grade;
import com.payper.global.exception.CustomIllegalArgumentException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeResponse {
	private Integer id;
	private Long start;
	private Long totalDiscount;

	public static GradeResponse toDTO(final Grade grade) {
		if(grade == null){
			throw new CustomIllegalArgumentException("grade");
		}

		return GradeResponse.builder()
				.id(grade.getGradeId())
				.start(grade.getStart())
				.totalDiscount(grade.getTotalDiscount())
				.build();
	}
}
