package com.example.bookmark.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 요청 DTO")
public record LoginRequest(
        @Schema(description = "아이디 또는 이메일", example = "bookmark123")
        @NotBlank(message = "아이디 또는 이메일은 필수입니다.")
        String identifier,

        @Schema(description = "비밀번호", example = "password123!")
        @NotBlank(message = "비밀번호는 필수입니다.")
        String password
) {}