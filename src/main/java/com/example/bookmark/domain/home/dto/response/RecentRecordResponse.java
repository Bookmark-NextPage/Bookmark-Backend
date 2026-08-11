package com.example.bookmark.domain.home.dto.response;

import com.example.bookmark.domain.record.entity.Record;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record RecentRecordResponse(
        Long recordId,
        String title,
        LocalDate createdAt
) {
    public static RecentRecordResponse from(Record record) {
        return new RecentRecordResponse(
                record.getId(),
                record.getTitle(),
                record.getCreatedAt().toLocalDate()
        );
    }
}