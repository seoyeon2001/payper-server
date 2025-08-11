package com.payper.domain.user;

import com.payper.domain.user.domain.UserCardTransaction;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserCardTransactionMapper {
    Integer save(UserCardTransaction userCardTransaction);

    UserCardTransaction get(Integer userCardTransactionId);
}
