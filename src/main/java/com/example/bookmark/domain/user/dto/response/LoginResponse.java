package com.example.bookmark.domain.user.dto.response;

import com.example.bookmark.domain.user.entity.User;

public record LoginResponse(
        Long userId,
        String name,
        String loginId
        // TODO: JWT 도입 시 accessToken 필드 추가
) {
    public static LoginResponse from(User user) {
        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getLoginId()
        );
    }
}