package com.example.webframework.auth.dto;

import com.example.webframework.user.dto.UserAccountResponse;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        UserAccountResponse user
) {}
