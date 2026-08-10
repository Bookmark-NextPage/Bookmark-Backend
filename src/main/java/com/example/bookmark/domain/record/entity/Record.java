package com.example.bookmark.domain.record.entity;

import com.example.bookmark.domain.bucketBoard.entity.BucketBoardMemo; // ✨ BucketBoardMemo import
import com.example.bookmark.domain.collectBook.entity.Chapter;
import com.example.bookmark.domain.record.entity.enums.RecordStatus;
import com.example.bookmark.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "records")
@Getter
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
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bucket_board_memo_id")
    private BucketBoardMemo bucketBoardMemo;

    @Column(length = 50)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecordStatus status;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordKeyword> recordKeywords = new ArrayList<>();

    @Builder
    public Record(User user, Chapter chapter, BucketBoardMemo bucketBoardMemo, String title, String content, RecordStatus status) {
        this.user = user;
        this.chapter = chapter;
        this.bucketBoardMemo = bucketBoardMemo;
        this.title = title;
        this.content = content;
        this.status = status;
    }

    public void updateRecord(Chapter chapter, String title, String content, RecordStatus status) {
        this.chapter = chapter;
        this.title = title;
        this.content = content;
        this.status = status;
    }

    public void addImage(RecordImage image) {
        this.images.add(image);
        image.assignRecord(this);
    }
}