package com.example.webframework.user.dto;

public record UserAccountUpdateRequest(String email, String nickname, String passwordHash) {}