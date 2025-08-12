package com.payper.global.notification.service;

import com.google.firebase.messaging.*;
import com.payper.domain.fcmToken.FcmTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private static final Set<String> FATAL =
            Set.of("UNREGISTERED","INVALID_ARGUMENT","SENDER_ID_MISMATCH");

    private final FirebaseMessaging firebaseMessaging;
    private final FcmTokenService fcmTokenService;

    /**
     * userId의 ACTIVE 토큰들에 멀티캐스트 전송
     */
    public boolean sendToUser(Integer userId,
                              Notification notification,
                              Map<String, String> data) {
        List<String> tokens = fcmTokenService.findActiveTokensByUserId(userId);
        if (tokens == null || tokens.isEmpty()) {
            log.warn("사용자 ID {}의 FCM 토큰이 활성화되어 있지 않습니다.", userId);
            return false;
        }

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(notification)
                .putAllData(data == null ? Map.of() : data)
                .addAllTokens(tokens)
                .build();

        try {
            BatchResponse br = firebaseMessaging.sendEachForMulticast(message);
            boolean anySuccess = false;

            for (int i = 0; i < tokens.size(); i++) {
                String tk = tokens.get(i);
                SendResponse r = br.getResponses().get(i);

                if (r.isSuccessful()) {
                    anySuccess = true;
                    log.info("FCM 메시지를 전송합니다. messageId = {}", r.getMessageId());
                    continue;
                }

                FirebaseMessagingException ex = r.getException();
                String code = (ex != null && ex.getMessagingErrorCode() != null)
                        ? ex.getMessagingErrorCode().name()
                        : "UNKNOWN";

                log.warn("FCM 메시지 전송을 실패했습니다. token={}, code={}", tk, code, ex);

                if (FATAL.contains(code)) {
                    fcmTokenService.markUnregistered(tk);
                }
            }
            return anySuccess;
        } catch (FirebaseMessagingException e) {
            log.error("FCM 멀티캐스트 전송을 실패했습니다. ", e);
            return false;
        }
    }

    /**
     * 단건 전송 (기존 시그니처 유지). 실패 시 토큰 비활성화.
     */
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
        } catch (FirebaseMessagingException e) {
            String code = (e.getMessagingErrorCode() != null)
                    ? e.getMessagingErrorCode().name()
                    : "UNKNOWN";
            log.warn("FCM 단건 전송을 실패했습니다. token={}, code={}", fcmToken, code, e);

            if (FATAL.contains(code)) {
                fcmTokenService.markUnregistered(fcmToken);
            }
            return false;
        } catch (Exception e) {
            log.warn("FCM 메시지 전송을 실패했습니다. token={}", fcmToken, e);
            return false;
        }
    }
}
