package com.example.webframework.user.dto;

import com.example.webframework.user.UserAccount;

public record UserAccountResponse(Long id, String email, String nickname) {
    public static UserAccountResponse from(UserAccount userAccount, String s) {
        return new UserAccountResponse(userAccount.getId(), userAccount.getEmail(), userAccount.getNickname());
    }
}