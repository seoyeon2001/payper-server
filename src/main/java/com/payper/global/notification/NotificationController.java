package com.payper.global.notification;

import com.google.firebase.messaging.Notification;
import com.payper.domain.partner.dto.PartnerKeywordSearchRequest;
import com.payper.domain.user.UserService;
import com.payper.global.notification.dto.NotificationRequest;
import com.payper.global.notification.dto.NotificationResponse;
import com.payper.global.notification.service.FcmService;
import com.payper.global.notification.service.NotificationService;
import com.payper.global.security.domain.CustomUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
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
            @AuthenticationPrincipal CustomUser customUser,
            @RequestBody PartnerKeywordSearchRequest request
    ) {
        Integer userId = userService.getUserId(customUser);
        Optional<Notification> notification = notificationService.buildPartnerNotification(userId, request);
        if (notification.isEmpty()) {
            return ResponseEntity.ok(new NotificationResponse(true, false));
        }

        boolean sent = fcmService.sendToUser(userId, notification.get(), Map.of());
        return ResponseEntity.ok(new NotificationResponse(true, sent));
    }
}
