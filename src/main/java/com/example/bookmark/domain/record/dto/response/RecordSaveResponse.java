package com.example.bookmark.domain.record.dto.response;

import com.example.bookmark.domain.record.entity.Record;
import com.example.bookmark.domain.record.entity.RecordImage;
import com.example.bookmark.domain.record.entity.enums.RecordStatus;
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
    private Long memoId;
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
                .memoId(record.getMemoId())
                .title(record.getTitle())
                .content(record.getContent())
                .imageUrls(record.getImages().stream()
                        .map(RecordImage::getImageUrl)
                        .toList())
                .keywordIds(record.getRecordKeywords().stream()
                        .map(rk -> rk.getKeyword().getId())
                        .toList())
                .isDraft(record.getStatus() == RecordStatus.DRAFT)
                .createdAt(record.getCreatedAt())
                .build();
    }
}