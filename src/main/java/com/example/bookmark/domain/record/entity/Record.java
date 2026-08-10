package com.example.bookmark.domain.record.entity;

import com.example.bookmark.domain.collectBook.entity.Chapter;
import com.example.bookmark.domain.record.entity.enums.RecordStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
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
    @JoinColumn(name = "chapter_id", nullable = true) // DRAFT 상태 시 null 허용
    private Chapter chapter;

    // TODO : 메모 연결 필요 !!!
    @Column(name = "memo_id")
    private Long memoId;

    @Column(length = 50)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "ai_image_url", length = 500)
    private String aiImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RecordStatus status;

    @Column(nullable = false)
    private Long likes = 0L;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordKeyword> recordKeywords = new ArrayList<>();

    @Builder
    public Record(Chapter chapter, Long memoId, String title, String content, RecordStatus status) {
        this.chapter = chapter;
        this.memoId = memoId;
        this.title = title;
        this.content = content;
        this.status = status != null ? status : RecordStatus.DRAFT;
        this.likes = 0L;
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