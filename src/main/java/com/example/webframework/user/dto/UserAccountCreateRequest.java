package com.example.webframework.user.dto;

public record UserAccountCreateRequest(String email, String passwordHash, String nickname) {}