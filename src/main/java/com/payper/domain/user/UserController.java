package com.payper.domain.user;

import com.payper.domain.user.dto.UserResponse;
import com.payper.global.security.domain.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo(@AuthenticationPrincipal CustomUser customUser) {
        Integer userId = userService.getUserId(customUser);
        log.info("유저 정보 조회 - userId : {} ", userId);

        UserResponse userInfo = userService.getMyInfo(userId);
        return ResponseEntity.ok(userInfo);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal CustomUser customUser) {
        userService.deleteUser(userService.getUserId(customUser));
        return ResponseEntity.ok().build();
    }
}
