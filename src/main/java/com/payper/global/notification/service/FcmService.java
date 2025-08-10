package com.payper.global.notification.service;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final FirebaseMessaging firebaseMessaging;

    public boolean send(Notification notification,
                        String fcmToken) {
        if (fcmToken == null || fcmToken.isEmpty()) {
            return false;
        }
        try {
            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(notification)
                    .build();

            String messageId = firebaseMessaging.send(message);
            log.info("FCM 메시지를 전송합니다. : messageId = {}", messageId);
            return true;
        } catch (Exception e) {
            log.warn("FCM 메시지 전송을 실패했습니다. ", e);
            return false;
        }
    }
}
