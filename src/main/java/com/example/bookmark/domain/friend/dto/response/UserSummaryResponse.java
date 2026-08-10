package com.example.bookmark.domain.friend.dto.response;

import com.example.bookmark.domain.user.entity.User;

public record UserSummaryResponse(
        Long userId,
        String name,
        String loginId,
        String profileImageUrl
) {
    public static UserSummaryResponse from(User user) {
        return new UserSummaryResponse(
                user.getId(),
                user.getName(),
                user.getLoginId(),
                user.getProfileImageUrl()
        );
    }
}