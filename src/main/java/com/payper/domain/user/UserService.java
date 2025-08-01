package com.payper.domain.user;

import com.payper.domain.user.dto.UserResponse;
import com.payper.domain.user.exception.NoSuchUserException;
import com.payper.global.security.domain.CustomUser;
import com.payper.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    // 이거 없애야함 - 서연이 할거임
    public User getUserById(Integer userId) {
        return userMapper.get(userId);
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
        int updated = userMapper.updateConnectedId(userId, connectedId);
        if (updated == 0) {
            throw new IllegalStateException("ConnectedId 업데이트 실패");
        }
    }

    public UserResponse getMyInfo(Integer userId) {
        UserResponse userInfo = userMapper.getUserInfo(userId);
        if (userInfo == null) {
            throw new NoSuchUserException();
        }
        return userInfo;
    }
}
