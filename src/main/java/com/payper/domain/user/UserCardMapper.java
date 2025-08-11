package com.payper.domain.user;

import com.payper.domain.user.domain.UserCard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserCardMapper {
    UserCard getByUserIdAndCardId(@Param("userId")Integer userId, @Param("cardId")Integer cardId);

    Integer getUserCardId(@Param("codefCardName") String codefCardName);
}
