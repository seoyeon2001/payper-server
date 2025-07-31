package com.payper.security.mapper;

import com.payper.user.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserDetailsMapper {
    public User
    get(@Param("userId")String userId);
}
