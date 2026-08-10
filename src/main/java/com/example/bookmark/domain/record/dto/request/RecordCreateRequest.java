package com.example.bookmark.domain.record.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "최종 기록 생성 요청 DTO")
public class RecordCreateRequest {

    @Schema(description = "임시저장 데이터를 불러와서 작성한 경우 해당 Record ID (신규 작성이면 null)", example = "1")
    private Long recordId;

    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    @Schema(description = "기록 제목", example = "혼자 떠난 첫 교토 여행")
    private String title;

    @NotBlank(message = "본문은 필수 입력 항목입니다.")
    @Schema(description = "기록 본문", example = "그날의 기분과 장면을 자유롭게 적어보았어요.")
    private String content;

    @Schema(description = "업로드할 이미지 URL 리스트 (최대 5장)")
    private List<String> imageUrls;

    @Schema(description = "선택한 감성 키워드 ID 리스트")
    private List<Long> keywordIds;
}