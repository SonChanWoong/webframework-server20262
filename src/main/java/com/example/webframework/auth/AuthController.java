package com.example.webframework.auth;

import com.example.webframework.auth.dto.LoginRequest;
import com.example.webframework.auth.dto.LoginResponse;
import com.example.webframework.user.UserAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/auth", "/user-account"})
@RequiredArgsConstructor
public class AuthController {
    private final UserAccountService userAccountService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse result = userAccountService.login(request);
        ResponseCookie cookie = ResponseCookie.from("accessToken", result.accessToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(result.expiresIn())
                .build();
        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body(result);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true).secure(false).sameSite("Lax").path("/").maxAge(0).build();
        return ResponseEntity.noContent().header("Set-Cookie", cookie.toString()).build();
    }

    @GetMapping("/me")
    public ResponseEntity<Void> me(Authentication authentication) {
        return ResponseEntity.ok().build();
    }
}
