package com.payper.domain.fcmToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmTokenService {
    private final FcmTokenMapper fcmTokenMapper;

    @Transactional
    public void register(Integer userId, String token){
        fcmTokenMapper.upsert(userId, token);
    }

    public List<String> findActiveTokensByUserId(Integer userId){
        return fcmTokenMapper.findActiveTokensByUserId(userId);
    }

    @Transactional
    public void markUnregistered(String token) {
        fcmTokenMapper.updateStatus(token, "INACTIVE");
        log.info("해당 token {}이 INACTIVE 처리되었습니다.", token);
    }
}
