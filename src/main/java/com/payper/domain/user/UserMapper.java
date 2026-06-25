package com.payper.domain.user;

import com.payper.domain.user.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User findById(@Param("userId") Integer userId);

    User findByUsername(@Param("username") String username);

    Integer countByUsername(@Param("username") String username);

    Integer updateConnectedId(@Param("userId") Integer userId, @Param("connectedId") String connectedId);

    void save(User user);

    void softDeleteUser(Integer userId);

    String getConnectedId(Integer userId);

    Integer updateFcmToken(@Param("userId") Integer userId,
                        @Param("fcmToken") String fcmToken);
}
