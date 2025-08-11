package com.payper.domain.user;

import com.payper.domain.user.domain.UserCardTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserCardTransactionMapper {
    Integer save(UserCardTransaction userCardTransaction);

    Integer save2(@Param("userCardTransaction") UserCardTransaction userCardTransaction);
    UserCardTransaction get(Integer userCardTransactionId);
}
