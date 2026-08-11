package com.example.bookmark.domain.home.dto.response;

import java.util.List;

public record HomeResponse(
        String userName,
        long weeklyCompletedMemoCount,
        long planMemoCount,
        long collectBookCount,
        long recordCount,
        List<RecentRecordResponse> recentRecords
) {
}