package com.payper.domain.fcmToken;

import com.payper.domain.user.UserService;
import com.payper.global.notification.dto.NotificationRequest;
import com.payper.global.security.domain.CustomUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fcm")
public class FcmTokenController {
    private final FcmTokenService tokenService;
    private final UserService userService;

    @PostMapping("/register")
    public void register(@AuthenticationPrincipal CustomUser customUser,
                         @RequestBody NotificationRequest notificationRequest) {
        Integer userId = userService.getUserId(customUser);
        String fcmToken = notificationRequest.getFcmToken();

        tokenService.register(userId, fcmToken);
    }
}
