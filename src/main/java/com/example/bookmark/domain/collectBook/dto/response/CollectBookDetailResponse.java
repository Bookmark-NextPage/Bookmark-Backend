package com.example.bookmark.domain.collectBook.dto.response;

import com.example.bookmark.domain.collectBook.entity.Chapter;
import com.example.bookmark.domain.collectBook.entity.CollectBook;
import com.example.bookmark.domain.collectBook.entity.enums.BookColor;
import com.example.bookmark.domain.collectBook.entity.enums.ChapterType;
import com.example.bookmark.domain.collectBook.entity.enums.Visibility;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "콜렉트북 상세 조회 응답 DTO")
public record CollectBookDetailResponse(
        @Schema(description = "콜렉트북 ID", example = "1")
        Long collectBookId,

        @Schema(description = "책 제목", example = "2026 다이어리")
        String title,

        @Schema(description = "표지 색상", example = "PINK")
        BookColor bookColor,

        @Schema(description = "연도", example = "2026")
        Integer year,

        @Schema(description = "공개 범위", example = "PUBLIC")
        Visibility visibility,

        @Schema(description = "챕터 설정 타입", example = "MONTHLY")
        ChapterType chapterType,

        @Schema(description = "챕터 목록")
        List<ChapterResponse> chapters
) {
    public static CollectBookDetailResponse from(CollectBook collectBook) {
        List<ChapterResponse> chapterResponses = collectBook.getChapters().stream()
                .map(ChapterResponse::from)
                .toList();

        return new CollectBookDetailResponse(
                collectBook.getId(),
                collectBook.getTitle(),
                collectBook.getBookColor(),
                collectBook.getYear(),
                collectBook.getVisibility(),
                collectBook.getChapterType(),
                chapterResponses
        );
    }

    @Schema(description = "챕터 상세 응답 DTO")
    public record ChapterResponse(
            @Schema(description = "챕터 ID", example = "10")
            Long chapterId,

            @Schema(description = "순서", example = "11")
            Integer sequence,

            @Schema(description = "챕터 이름", example = "11월 · 행운을 빌어줘")
            String name,

            @Schema(description = "해당 챕터에 포함된 기록/버킷 개수", example = "3")
            Long recordCount
    ) {
        public static ChapterResponse from(Chapter chapter) {
            // TODO: 기록(Record) 엔티티 및 연관관계 완성 시 실제 개수를 집계하여 전달
            // 현재는 UI 와이어프레임 구조에 맞춰 0L 기본값으로 응답
            Long count = 0L;

            return new ChapterResponse(
                    chapter.getId(),
                    chapter.getSequence(),
                    chapter.getName(),
                    count
            );
        }
    }
}