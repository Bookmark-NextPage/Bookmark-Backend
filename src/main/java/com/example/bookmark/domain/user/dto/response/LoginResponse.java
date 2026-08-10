package com.example.bookmark.domain.user.dto.response;

import com.example.bookmark.domain.user.entity.User;

public record LoginResponse(
        Long userId,
        String name,
        String loginId,
        String accessToken
) {
    public static LoginResponse of(User user, String accessToken) {
        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getLoginId(),
                accessToken
        );
    }
}