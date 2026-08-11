package com.example.bookmark.domain.record.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI 감성 스크랩북 이미지 생성/재생성 응답 DTO")
public class AiImageGenerateResponse {

    @Schema(description = "생성된 AI 스크랩북 이미지 URL (미리보기용)", example = "https://storage.googleapis.com/...")
    private String tempImageUrl;
}