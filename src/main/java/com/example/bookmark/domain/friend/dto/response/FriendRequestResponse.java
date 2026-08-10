package com.example.bookmark.domain.friend.dto.response;

import com.example.bookmark.domain.user.entity.User;

public record FriendRequestResponse(
        Long requestId,   // 수락/거절 시 사용
        Long userId,      // 신청 보낸 사람
        String name,
        String loginId,
        String profileImageUrl
) {
    public static FriendRequestResponse of(Long requestId, User sender) {
        return new FriendRequestResponse(
                requestId,
                sender.getId(),
                sender.getName(),
                sender.getLoginId(),
                sender.getProfileImageUrl()
        );
    }
}