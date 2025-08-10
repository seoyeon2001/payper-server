package com.payper.global.notification;

import com.payper.domain.partner.dto.PartnerKeywordSearchRequest;
import com.payper.domain.user.UserService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {
    private final NotificationService notificationService;
    private final UserService userService;

    @PostMapping("/send")
    public ResponseEntity<?> sendNotification(
            //@AuthenticationPrincipal CustomUser customUser,
            PartnerKeywordSearchRequest request
            // @RequestBody Map<String, String> requestBody
            ) {
        //Integer userId = userService.getUserId(customUser);
        Integer userId = 1;
        //String fcmToken = requestBody.get("fcmToken");
        //System.out.println(fcmToken);

        boolean sent = notificationService.sendToUser(userId, request);
        return ResponseEntity.ok(Map.of("ok", true, "sent", sent));
    }
}
