package com.example.bookmark.domain.user.dto.response;

import com.example.bookmark.domain.collectBook.entity.enums.BookColor;
import com.example.bookmark.domain.collectBook.entity.enums.Visibility;

import java.util.List;

public record MyPageResponse(
        Profile profile,
        Stats stats,
        List<RecentBook> recentBooks,
        List<Friend> friends,
        Boolean aiUse,
        Boolean isInAppNotificationEnabled
) {
    public record Profile(
            Long userId,
            String name,
            String loginId,
            String bio,
            String profileImageUrl
    ) {}

    public record Stats(
            long totalRecords,      // 총 기록
            long completedBuckets,  // 완료한 버킷
            long collectBookCount,  // 콜렉트북 수
            long friendCount        // 친구 수
    ) {}

    public record RecentBook(
            Long collectBookId,
            Integer year,
            String title,
            BookColor bookColor,
            Visibility visibility
    ) {}

    public record Friend(
            Long userId,
            String name,
            String loginId,
            String profileImageUrl
    ) {}
}