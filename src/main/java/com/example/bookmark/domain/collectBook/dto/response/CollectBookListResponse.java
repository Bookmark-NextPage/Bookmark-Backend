package com.example.bookmark.domain.collectBook.dto.response;

import com.example.bookmark.domain.collectBook.entity.CollectBook;
import com.example.bookmark.domain.collectBook.entity.enums.BookColor;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "콜렉트북 목록 조회 응답 DTO")
public record CollectBookListResponse(
        @Schema(description = "콜렉트북 ID", example = "1")
        Long collectBookId,

        @Schema(description = "책 제목", example = "2026 다이어리")
        String title,

        @Schema(description = "연도", example = "2026")
        Integer year,

        @Schema(description = "표지 색상", example = "PINK")
        BookColor bookColor
) {
    public static CollectBookListResponse from(CollectBook collectBook) {
        return new CollectBookListResponse(
                collectBook.getId(),
                collectBook.getTitle(),
                collectBook.getYear(),
                collectBook.getBookColor()
        );
    }
}