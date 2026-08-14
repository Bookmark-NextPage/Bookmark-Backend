package com.example.bookmark.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "회원가입 요청 DTO")
public record SignupRequest(
        @Schema(description = "이름", example = "김최고")
        @NotBlank(message = "이름은 필수입니다.")
        @Size(max = 20, message = "이름은 최대 20자까지 입력 가능합니다.")
        String name,

        @Schema(description = "아이디", example = "bookmark123")
        @NotBlank(message = "아이디는 필수입니다.")
        @Size(min = 4, max = 30, message = "아이디는 4~30자여야 합니다.")
        String loginId,

        @Schema(description = "이메일", example = "user@bookmark.com")
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @Schema(description = "비밀번호", example = "password123!")
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, max = 64, message = "비밀번호는 8자 이상이어야 합니다.")
        String password,

        @Schema(description = "비밀번호 확인", example = "password123!")
        @NotBlank(message = "비밀번호 확인은 필수입니다.")
        String passwordConfirm
) {}