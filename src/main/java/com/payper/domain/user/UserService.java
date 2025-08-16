package com.payper.domain.user;

import com.payper.domain.user.dto.UserReportResponse;
import com.payper.domain.user.dto.UserResponse;
import com.payper.domain.user.dto.UserTransactionSummaryDto;
import com.payper.domain.user.exception.NoSuchUserException;
import com.payper.external.codef.exception.ConnectedIdUpdateFailedException;
import com.payper.external.gpt.OpenAIExtractService;
import com.payper.global.security.domain.CustomUser;
import com.payper.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserMapper userMapper;
    private final UserCardTransactionMapper userCardTransactionMapper;
    private final OpenAIExtractService openAIExtractService;

    // 임시
    public final static String startDate= "20250701";
    public final static String endDate= "20250731";

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
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new NoSuchUserException();
        }
        return UserResponse.toDto(user);
    }

    @Transactional
    public void deleteUser(Integer userId) {
        userMapper.softDeleteUser(userId);
    }

    @Transactional
    public void updateFcmToken(Integer userId, String fcmToken) {
        userMapper.updateFcmToken(userId, fcmToken);
    }

    @Transactional
    public UserReportResponse analyzeMonthWithGpt(Integer userId) {
        List<UserTransactionSummaryDto> userTransactionSummaryDto
                = userCardTransactionMapper.getUserTransactionSummaryDto(userId, startDate, endDate);

        return openAIExtractService.extract(userTransactionSummaryDto);
    }
}
