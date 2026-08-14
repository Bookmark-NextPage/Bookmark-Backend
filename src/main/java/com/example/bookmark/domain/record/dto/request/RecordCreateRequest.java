package com.example.bookmark.domain.record.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "기록 생성 요청 DTO")
public class RecordCreateRequest {

    @Schema(description = "콜렉트북 챕터 ID (일반 챕터 기록일 경우)", example = "1")
    private Long chapterId;

    @Schema(description = "버킷보드 메모지 ID (메모지 기반 기록일 경우)", example = "5")
    private Long bucketBoardMemoId;

    @NotBlank(message = "제목은 필수 입력 사항입니다.")
    @Schema(description = "기록 제목", example = "성수동 카페 투어")
    private String title;

    @NotBlank(message = "본문 내용은 필수 입력 사항입니다.")
    @Schema(description = "기록 내용", example = "오랜만에 친구들이랑 디저트 먹은 날!")
    private String content;

    @Schema(description = "AI가 생성한 이미지 URL (AI Off이거나 미사용 시 null)", example = "data:image/png;base64,iVBOR...")
    private String aiImageUrl; // Nullable

    @Schema(description = "선택한 감성 키워드 ID 목록 (없을 경우 null 또는 빈 리스트)", example = "[10, 13]")
    private List<Long> keywordIds;

    @Schema(description = "사용자가 직접 업로드한 일반 이미지 URL 목록 (최대 5장)")
    private List<String> imageUrls;
}