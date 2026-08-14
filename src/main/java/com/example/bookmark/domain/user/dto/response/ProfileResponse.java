package com.example.bookmark.domain.user.dto.response;

import com.example.bookmark.domain.user.entity.User;

public record ProfileResponse(
        Long userId,
        String name,
        String loginId,
        String bio,
        String profileImageUrl
) {
    public static ProfileResponse from(User user) {
        return new ProfileResponse(
                user.getId(),
                user.getName(),
                user.getLoginId(),
                user.getBio(),
                user.getProfileImageUrl()
        );
    }
}