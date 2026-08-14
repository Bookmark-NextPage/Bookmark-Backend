package com.example.bookmark.domain.record.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "기록 임시 저장 요청 DTO")
public class RecordDraftSaveRequest {

    @Schema(description = "이전에 임시저장했던 Record ID (처음 임시저장 시 null)", example = "1")
    private Long recordId;

    @Schema(description = "제목 (선택)", example = "교토 여행 첫날")
    private String title;

    @Schema(description = "본문 (선택)", example = "날씨가 맑았다.")
    private String content;

    @Schema(description = "이미지 URL 리스트 (선택)")
    private List<String> imageUrls;

    @Schema(description = "감성 키워드 ID 리스트 (선택)")
    private List<Long> keywordIds;
}