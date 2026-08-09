package com.example.bookmark.domain.user.dto.response;

import com.example.bookmark.domain.user.entity.User;

public record SignupResponse(
        Long userId,
        String name,
        String loginId,
        String email
) {
    public static SignupResponse from(User user) {
        return new SignupResponse(
                user.getId(),
                user.getName(),
                user.getLoginId(),
                user.getEmail()
        );
    }
}