package com.payper.user.mapper;

import com.payper.user.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    // 회원 상세 조회
    User get(@Param("userId") Integer userId);

    // connected id 등록
    int updateConnectedId(@Param("userId") Integer userId, @Param("connectedId") String connectedId);
}
