package com.example.bookmark.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "프로필 편집 요청 DTO")
public record ProfileUpdateRequest(
        @Schema(description = "이름", example = "다인")
        @NotBlank(message = "이름은 필수입니다.")
        @Size(max = 20, message = "이름은 최대 20자까지 입력 가능합니다.")
        String name,

        @Schema(description = "한줄소개", example = "사소한 것에도 성취감을 느끼는 사람")
        @Size(max = 100, message = "한줄소개는 최대 100자까지 입력 가능합니다.")
        String bio,

        @Schema(description = "프로필 사진 URL", example = "https://.../profile.png")
        String profileImageUrl
) {}