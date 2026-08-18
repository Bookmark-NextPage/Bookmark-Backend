package com.example.bookmark.domain.record.dto.response;

import java.time.LocalDateTime;

public record RecordSearchResponse(
        Long recordId,
        String title,             // null 가능
        String chapterName,       // 도전
        String collectBookTitle,  // 어느 책의 기록인지
        LocalDateTime createdAt
) {
}