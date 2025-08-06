package com.payper.domain.user;

import com.payper.domain.user.dto.UserResponse;
import com.payper.domain.user.exception.NoSuchUserException;
import com.payper.external.codef.exception.ConnectedIdUpdateFailedException;
import com.payper.global.security.domain.CustomUser;
import com.payper.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    public String getConnectedIdById(Integer userId) {
        return userMapper.getConnectedId(userId);
    }

    public Integer getUserId(CustomUser customUser) {
        Integer userId = customUser.getUser().getUserId();

        if (userId == null) {
            throw new NoSuchUserException();
        }

        return userId;
    }

    @Transactional
    public void updateConnectedId(Integer userId, String connectedId) {
        Integer updated = userMapper.updateConnectedId(userId, connectedId);

        if (updated != 1) {
            throw new ConnectedIdUpdateFailedException(userId);
        }
    }

    public UserResponse getMyInfo(Integer userId) {
        UserResponse userInfo = userMapper.getUserInfo(userId);
        if (userInfo == null) {
            throw new NoSuchUserException();
        }
        return userInfo;
    }

    @Transactional
    public void deleteUser(Integer userId) {
        userMapper.softDeleteUser(userId);
    }
}
