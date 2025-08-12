package com.payper.domain.fcmToken;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FcmTokenMapper {
    void upsert(@Param("userId") Integer userId,
                @Param("token") String token);

    List<String> findActiveTokensByUserId(@Param("userId") Integer userId);

    Integer updateStatus(@Param("token") String token,
                     @Param("status") String status);
}
