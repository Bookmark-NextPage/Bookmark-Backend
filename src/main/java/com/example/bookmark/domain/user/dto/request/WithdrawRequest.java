package com.example.bookmark.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "회원 탈퇴 요청 DTO")
public record WithdrawRequest(
        @Schema(description = "비밀번호 재확인", example = "password123!")
        @NotBlank(message = "비밀번호는 필수입니다.")
        String password
) {}