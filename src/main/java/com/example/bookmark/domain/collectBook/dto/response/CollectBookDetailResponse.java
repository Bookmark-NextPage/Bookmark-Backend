package com.example.bookmark.domain.collectBook.dto.response;

import com.example.bookmark.domain.collectBook.entity.Chapter;
import com.example.bookmark.domain.collectBook.entity.CollectBook;
import com.example.bookmark.domain.collectBook.entity.enums.BookColor;
import com.example.bookmark.domain.collectBook.entity.enums.ChapterType;
import com.example.bookmark.domain.collectBook.entity.enums.Visibility;
import com.example.bookmark.domain.record.entity.Record;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@Schema(description = "콜렉트북 상세 조회 응답 DTO")
public record CollectBookDetailResponse(
        @Schema(description = "콜렉트북 ID", example = "1")
        Long collectBookId,

        @Schema(description = "책 제목", example = "2026 다이어리")
        String title,

        @Schema(description = "표지 색상", example = "PINK")
        BookColor bookColor,

        @Schema(description = "연도", example = "2026")
        Integer year,

        @Schema(description = "공개 범위", example = "PUBLIC")
        Visibility visibility,

        @Schema(description = "챕터 설정 타입", example = "MONTHLY")
        ChapterType chapterType,

        @Schema(description = "챕터 목록")
        List<ChapterResponse> chapters
) {
    public static CollectBookDetailResponse of(CollectBook collectBook, Map<Long, List<Record>> recordMapByChapter) {
        List<ChapterResponse> chapterResponses = collectBook.getChapters().stream()
                .map(chapter -> {
                    List<Record> records = recordMapByChapter.getOrDefault(chapter.getId(), List.of());
                    return ChapterResponse.of(chapter, records);
                })
                .toList();

        return new CollectBookDetailResponse(
                collectBook.getId(),
                collectBook.getTitle(),
                collectBook.getBookColor(),
                collectBook.getYear(),
                collectBook.getVisibility(),
                collectBook.getChapterType(),
                chapterResponses
        );
    }

    @Schema(description = "챕터 상세 응답 DTO")
    public record ChapterResponse(
            @Schema(description = "챕터 ID", example = "10")
            Long chapterId,

            @Schema(description = "순서", example = "11")
            Integer sequence,

            @Schema(description = "챕터 이름", example = "11월 · 행운을 빌어줘")
            String name,

            @Schema(description = "해당 챕터에 포함된 기록 개수", example = "3")
            Long recordCount,

            @Schema(description = "해당 챕터에 포함된 기록 목록")
            List<RecordSummaryResponse> records
    ) {
        public static ChapterResponse of(Chapter chapter, List<Record> records) {
            List<RecordSummaryResponse> recordSummaries = records.stream()
                    .map(RecordSummaryResponse::from)
                    .toList();

            return new ChapterResponse(
                    chapter.getId(),
                    chapter.getSequence(),
                    chapter.getName(),
                    (long) records.size(),
                    recordSummaries
            );
        }
    }

    @Schema(description = "챕터 내 기록 요약 응답 DTO")
    public record RecordSummaryResponse(
            @Schema(description = "기록 ID", example = "101")
            Long recordId,

            @Schema(description = "기록 제목", example = "교토 단풍 시즌에 혼자 여행 가기")
            String title,

            @Schema(description = "기록 내용 요약", example = "3박 4일 내내 걸었다. 처음으로 여행이 안 무서웠다.")
            String content,

            @Schema(description = "대표 이미지 URL", example = "https://s3.bucket.com/image.jpg")
            String imageUrl,

            @Schema(description = "키워드/태그 목록", example = "[\"도전\", \"여행\"]")
            List<String> keywords
    ) {
        public static RecordSummaryResponse from(Record record) {
            // 대표 이미지: AI 생성 이미지가 있으면 우선 사용, 없으면 업로드된 첫 번째 일반 이미지 사용
            String mainImage = record.getAiImageUrl();
            if (mainImage == null && record.getImages() != null && !record.getImages().isEmpty()) {
                mainImage = record.getImages().get(0).getImageUrl();
            }

            // 키워드 이름 리스트 추출
            List<String> keywordList = record.getRecordKeywords() != null ?
                    record.getRecordKeywords().stream()
                            .map(rk -> rk.getKeyword().getName())
                            .toList() : List.of();

            return new RecordSummaryResponse(
                    record.getId(),
                    record.getTitle(),
                    record.getContent(),
                    mainImage,
                    keywordList
            );
        }
    }
}