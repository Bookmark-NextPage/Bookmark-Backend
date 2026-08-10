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
@Schema(description = "기록 상세 페이지 조회 응답 DTO")
public class RecordDetailResponse {

    @Schema(description = "기록 ID", example = "1")
    private Long recordId;

    @Schema(description = "콜렉트북 ID", example = "10")
    private Long collectBookId;

    @Schema(description = "챕터 ID", example = "11")
    private Long chapterId;

    @Schema(description = "챕터 이름", example = "Chapter 11")
    private String chapterName;

    @Schema(description = "작성일자 (YYYY.MM.DD)", example = "2025.11.14")
    private LocalDateTime recordCreatedAt;

    @Schema(description = "기록 제목", example = "교토 단풍 시즌에 혼자 여행 가기")
    private String title;

    @Schema(description = "기록 본문")
    private String content;

    @Schema(description = "감성 키워드 이름 리스트", example = "[\"도전\"]")
    private List<String> keywords;

    @Schema(description = "사용자가 직접 업로드한 이미지 URL 목록")
    private List<String> imageUrls;

    @Schema(description = "AI가 생성한 감성 스크랩북 이미지 URL")
    private String aiImageUrl;

    @Schema(description = "좋아요 개수", example = "12")
    private Long likeCount;

    @Schema(description = "현재 로그인한 유저의 좋아요 여부", example = "true")
    private Boolean isLiked;

    @Schema(description = "댓글 개수", example = "2")
    private Integer commentCount;

    @Schema(description = "댓글 목록")
    private List<CommentResponse> comments;

    @Getter
    @AllArgsConstructor
    @Builder
    @Schema(description = "기록 댓글 응답 DTO")
    public static class CommentResponse {
        @Schema(description = "댓글 ID", example = "100")
        private Long commentId;

        @Schema(description = "댓글 작성자 ID", example = "5")
        private Long userId;

        @Schema(description = "댓글 작성자 닉네임", example = "지우")
        private String nickname;

        @Schema(description = "댓글 내용", example = "혼자 여행 진짜 용기 있다!!")
        private String content;

        @Schema(description = "댓글 작성 일시")
        private LocalDateTime createdAt;
    }

    public static RecordDetailResponse of(
            Record record,
            Long likeCount,
            Boolean isLiked,
            List<CommentResponse> comments
    ) {
        return RecordDetailResponse.builder()
                .recordId(record.getId())
                .collectBookId(record.getChapter() != null && record.getChapter().getCollectBook() != null
                        ? record.getChapter().getCollectBook().getId() : null)
                .chapterId(record.getChapter() != null ? record.getChapter().getId() : null)
                .chapterName(record.getChapter() != null ? record.getChapter().getName() : null)
                .recordCreatedAt(record.getCreatedAt())
                .title(record.getTitle())
                .content(record.getContent())
                .keywords(record.getRecordKeywords().stream()
                        .map(rk -> rk.getKeyword().getName())
                        .toList())
                .imageUrls(record.getImages().stream()
                        .map(RecordImage::getImageUrl)
                        .toList())
                .aiImageUrl(record.getAiImageUrl()) // ERD상 record 테이블의 ai_image_url
                .likeCount(likeCount != null ? likeCount : 0L)
                .isLiked(isLiked != null ? isLiked : false)
                .commentCount(comments != null ? comments.size() : 0)
                .comments(comments)
                .build();
    }
}