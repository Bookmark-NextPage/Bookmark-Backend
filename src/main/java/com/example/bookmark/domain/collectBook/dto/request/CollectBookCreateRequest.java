package com.example.bookmark.domain.collectBook.dto.request;

import com.example.bookmark.domain.collectBook.entity.enums.BookColor;
import com.example.bookmark.domain.collectBook.entity.enums.ChapterType;
import com.example.bookmark.domain.collectBook.entity.enums.Visibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Schema(description = "콜렉트북 생성 요청 DTO")
public record CollectBookCreateRequest(
        @Schema(description = "책 제목", example = "2026 다이어리")
        @NotBlank(message = "책 제목은 필수입니다.")
        @Size(max = 10, message = "책 제목은 최대 10자까지 입력 가능합니다.")
        String title,

        @Schema(description = "표지 색상", example = "PINK")
        @NotNull(message = "표지 색상은 필수입니다.")
        BookColor coverColor,

        @Schema(description = "연도", example = "2026")
        @NotNull(message = "연도는 필수입니다.")
        Integer year,

        @Schema(description = "공개 범위", example = "PUBLIC")
        Visibility visibility,

        @Schema(description = "챕터 설정 타입 (MONTHLY: 월 단위 12개, CUSTOM: 직접 설정)", example = "MONTHLY")
        @NotNull(message = "챕터 설정 타입은 필수입니다.")
        ChapterType chapterType,

        @Schema(description = "챕터 목록 (MONTHLY: 12개 필수, CUSTOM: 1~20개)")
        @NotNull(message = "챕터 목록은 필수입니다.")
        @Size(min = 1, max = 20, message = "챕터는 최소 1개에서 최대 20개까지 설정 가능합니다.")
        List<@Valid ChapterCreateRequest> chapters
) {
        @Schema(description = "챕터 생성 요청 DTO")
        public record ChapterCreateRequest(
                @Schema(description = "챕터 이름", example = "1월 - 시작")
                @NotBlank(message = "챕터 이름은 필수입니다.")
                @Size(max = 10, message = "챕터 이름은 최대 10자까지 입력 가능합니다.")
                String name
        ) {}
}