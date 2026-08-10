package com.example.bookmark.domain.collectBook.dto.request;

import com.example.bookmark.domain.collectBook.entity.enums.Visibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "콜렉트북 공개 범위 수정 요청 DTO")
public record CollectBookVisibilityUpdateRequest(
        @Schema(description = "변경할 공개 범위 (기본값: PUBLIC)", example = "PUBLIC")
        @NotNull(message = "공개 범위는 필수 입력 값입니다.")
        Visibility visibility
) {
}