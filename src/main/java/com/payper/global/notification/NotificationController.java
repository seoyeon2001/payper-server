package com.payper.global.notification;

import com.google.firebase.messaging.Notification;
import com.payper.domain.partner.dto.PartnerKeywordSearchRequest;
import com.payper.domain.user.UserService;
import com.payper.global.notification.dto.NotificationRequest;
import com.payper.global.notification.dto.NotificationResponse;
import com.payper.global.notification.service.FcmService;
import com.payper.global.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {
    private final NotificationService notificationService;
    private final UserService userService;
    private final FcmService fcmService;

    @PostMapping("/send")
    public ResponseEntity<NotificationResponse> sendNotification(
            // TODO : @AuthenticationPrincipal CustomUser customUser,추가
            PartnerKeywordSearchRequest request,
            @RequestBody NotificationRequest notificationRequest
    ) {
        Integer userId = 1; // TODO : Integer userId = userService.getUserId(customUser); 변경

        String fcmToken = notificationRequest.getFcmToken();
        if (fcmToken == null || fcmToken.isBlank()) {
            return ResponseEntity.ok(new NotificationResponse(true, false));
        }

        userService.updateFcmToken(userId, fcmToken);

        Optional<Notification> notification = notificationService.buildPartnerNotification(userId, request);
        boolean sent = notification.isPresent() &&
                fcmService.send(notification.get(), fcmToken);

        return ResponseEntity.ok(new NotificationResponse(true, sent));
    }
}
