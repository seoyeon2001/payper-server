package com.payper.user.mapper;

import com.payper.user.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    public User
    findByOauthProviderAndOauthId(@Param("oauthProvider") String oauthProvider, @Param("oauthId") String oauthId); //select

    public User
    get(@Param("userId") Integer userId);

    public int
    updateConnectedId(@Param("userId") Integer userId, @Param("connectedId") String connectedId);

    public int
    createUser(@Param("user") User user); //insert
}