package com.payper.user.service;

import com.payper.security.domain.CustomUser;
import com.payper.user.domain.User;
import com.payper.user.mapper.UserMapper;
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
        return customUser.getUser().getUserId();
    }

    @Transactional
    public void updateConnectedId(Integer userId, String connectedId) {
        int updated = userMapper.updateConnectedId(userId, connectedId);
        if (updated == 0) {
            throw new IllegalStateException("ConnectedId 업데이트 실패");
        }
    }
}
