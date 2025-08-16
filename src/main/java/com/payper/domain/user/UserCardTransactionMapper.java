package com.payper.domain.user;

import com.payper.domain.user.domain.UserCardTransaction;
import com.payper.domain.user.dto.UserTransactionSummaryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserCardTransactionMapper {
    Integer save(UserCardTransaction userCardTransaction);

    UserCardTransaction get(Integer userCardTransactionId);

    boolean existsByApprovalInfo(String approvalNo, String usedDate, String usedTime, String usedAmount, Integer userCardId);

    List<UserTransactionSummaryDto> getUserTransactionSummaryDto(
            @Param("userId") Integer userId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
            );
}
