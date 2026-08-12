package com.example.bookmark.domain.record.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecordAiImageUpdateRequest {

    @NotBlank(message = "AI 이미지 URL은 필수 입력 항목입니다.")
    private String aiImageUrl;
}