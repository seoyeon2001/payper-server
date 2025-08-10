package com.payper.global.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final FirebaseMessaging firebaseMessaging;

    // 테스트용 하드코딩 토큰 (실제 토큰으로 교체)
    private static final String TEST_FCM_TOKEN = "여기에_실제_FCM_토큰_문자열";

    public boolean send(String title, String body) {
        try {
            Message message = Message.builder()
                    .setToken(TEST_FCM_TOKEN)
                    .setNotification(
                            Notification.builder()
                                    .setTitle(title)
                                    .setBody(body)
                                    .build())
                    .build();

            String messageId = firebaseMessaging.send(message);
            log.info("FCM 메시지를 보냅니다 : messageId = {}", messageId);
            return true;
        } catch (Exception e) {
            log.warn("FCM 메시지 전송을 실패했습니다. ", e);
            return false;
        }
    }
}
