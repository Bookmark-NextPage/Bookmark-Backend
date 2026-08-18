package com.example.bookmark.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "AI 기능 ON/OFF 설정 변경 요청 DTO")
public record AiUseUpdateRequest(
        @Schema(description = "AI 기능 사용 여부 (true: ON, false: OFF)", example = "true")
        @NotNull(message = "AI 사용 여부는 필수입니다.")
        Boolean aiUse
) {
}