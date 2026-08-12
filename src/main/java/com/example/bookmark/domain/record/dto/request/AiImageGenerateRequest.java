package com.example.bookmark.domain.record.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI 감성 스크랩북 이미지 생성/재생성 요청 DTO")
public class AiImageGenerateRequest {

    @Schema(description = "기록 제목", example = "혼자 떠난 첫 교토 여행")
    private String title;

    @Schema(description = "기록 본문", example = "그날의 기분과 장면을 자유롭게 적어보았어요.")
    private String content;

    @Size(max = 5, message = "이미지는 최대 5장까지 첨부할 수 있습니다.")
    @Schema(description = "사용자가 첨부한 이미지 URL 리스트 (최대 5장)")
    private List<String> imageUrls;

    @Schema(description = "선택한 감성 키워드 ID 리스트")
    private List<Long> keywordIds;

    @Schema(description = "이미지 재생성 요청 시 사용자 피드백 (첫 생성 시 null 또는 빈 문자열)", example = "색감을 더 화사하게 해주고 사진을 크게 배치해줘")
    private String feedback;

    @AssertTrue(message = "이미지는 jpg, jpeg, png 형식만 업로드할 수 있습니다.")
    public boolean isValidImageExtension() {

        if (imageUrls == null || imageUrls.isEmpty()) {
            return true;
        }

        return imageUrls.stream().allMatch(url -> {
            String lower = url.toLowerCase();
            return lower.endsWith(".jpg")
                    || lower.endsWith(".jpeg")
                    || lower.endsWith(".png");
        });
    }
}