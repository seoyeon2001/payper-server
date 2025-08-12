package com.payper.domain.user;

import com.payper.domain.user.domain.UserCardTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserCardTransactionMapper {
    Integer save(UserCardTransaction userCardTransaction);

    UserCardTransaction get(Integer userCardTransactionId);

    boolean existsByApprovalInfo(String approvalNo, String usedDate, String usedTime, String usedAmount, Integer userCardId);
}
