package com.payper.domain.user;

import com.payper.domain.user.domain.User;
import com.payper.domain.user.dto.CreateUserRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface UserMapper {
    Optional<User> findByOauthProviderAndOauthId(@Param("oauthProvider") String oauthProvider, @Param("oauthId") String oauthId); //select

    User findById(@Param("userId") Integer userId);

    Integer updateConnectedId(@Param("userId") Integer userId, @Param("connectedId") String connectedId);

    void save(User user);

    void softDeleteUser(Integer userId);

    String getConnectedId(Integer userId);

    Integer updateFcmToken(@Param("userId") Integer userId,
                        @Param("fcmToken") String fcmToken);
}