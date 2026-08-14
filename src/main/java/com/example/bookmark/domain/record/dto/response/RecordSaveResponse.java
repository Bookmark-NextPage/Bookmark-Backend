package com.example.bookmark.domain.record.dto.response;

import com.example.bookmark.domain.record.entity.Record;
import com.example.bookmark.domain.record.entity.RecordImage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "기록 저장/조회 응답 DTO")
public class RecordSaveResponse {

    private Long recordId;
    private Long chapterId;
    private Long memoId; // 클라이언트에 전달하는 JSON 필드명은 memoId로 유지
    private String title;
    private String content;
    private List<String> imageUrls;
    private List<Long> keywordIds;
    private Boolean isDraft;
    private LocalDateTime createdAt;

    public static RecordSaveResponse from(Record record) {
        return RecordSaveResponse.builder()
                .recordId(record.getId())
                .chapterId(record.getChapter() != null ? record.getChapter().getId() : null)
                .memoId(record.getBucketBoardMemo() != null ? record.getBucketBoardMemo().getBucketBoardMemoId() : null)
                .title(record.getTitle())
                .content(record.getContent())
                .imageUrls(record.getImages().stream()
                        .map(RecordImage::getImageUrl)
                        .toList())
                .keywordIds(record.getRecordKeywords().stream()
                        .map(rk -> rk.getKeyword().getId())
                        .toList())
                .createdAt(record.getCreatedAt())
                .build();
    }
}