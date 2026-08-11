package com.example.bookmark.domain.record.entity;

import com.example.bookmark.domain.bucketBoard.entity.BucketBoardMemo;
import com.example.bookmark.domain.collectBook.entity.Chapter;
import com.example.bookmark.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "records")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    // 메모지 기반 X인 경우 해당 컬럼 Null
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bucket_board_memo_id")
    private BucketBoardMemo bucketBoardMemo;

    @Column(length = 50)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "ai_image_url", length = 500)
    private String aiImageUrl;

    @Builder.Default
    @Column(nullable = false)
    private Long likes = 0L;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordImage> images = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordKeyword> recordKeywords = new ArrayList<>();

    @Builder
    public Record(User user, Chapter chapter, BucketBoardMemo bucketBoardMemo, String title, String content, String aiImageUrl) {
        this.user = user;
        this.chapter = chapter;
        this.bucketBoardMemo = bucketBoardMemo;
        this.title = title;
        this.content = content;
        this.aiImageUrl = aiImageUrl;
        this.likes = 0L;
    }

    public void updateRecord(Chapter chapter, String title, String content) {
        this.chapter = chapter;
        this.title = title;
        this.content = content;
    }

    public void updateAiImageUrl(String aiImageUrl) {
        this.aiImageUrl = aiImageUrl;
    }

    public void addImage(RecordImage image) {
        this.images.add(image);
        image.assignRecord(this);
    }

    // 작성된 기록 안에 사용된 키워드 추가
    public void addRecordKeyword(RecordKeyword recordKeyword) {
        this.recordKeywords.add(recordKeyword);
        recordKeyword.assignRecord(this);
    }
}