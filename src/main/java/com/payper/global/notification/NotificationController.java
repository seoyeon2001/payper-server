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
            //@AuthenticationPrincipal CustomUser customUser,
            PartnerKeywordSearchRequest request,
            @RequestBody NotificationRequest notificationRequest
    ) {
        //Integer userId = userService.getUserId(customUser);
        Integer userId = 1;

        Optional<Notification> notification = notificationService.buildPartnerNotification(userId, request);
        boolean sent = notification.isPresent() &&
                fcmService.send(notification.get(), notificationRequest.getFcmToken());

        return ResponseEntity.ok(new NotificationResponse(true, sent));
    }
}
