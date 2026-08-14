package com.example.bookmark.domain.collectBook.entity;

import com.example.bookmark.domain.collectBook.entity.enums.BookColor;
import com.example.bookmark.domain.collectBook.entity.enums.ChapterType;
import com.example.bookmark.domain.collectBook.entity.enums.CollectBookType;
import com.example.bookmark.domain.collectBook.entity.enums.Visibility;
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

// 생성/수정 시점을 자동으로 기록함.
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "collect_book")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CollectBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collect_book_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 10)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "book_color", nullable = false)
    private BookColor bookColor;

    @Column(nullable = false)
    private Integer year;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility")
    private Visibility visibility = Visibility.PUBLIC;

    @Enumerated(EnumType.STRING)
    @Column(name = "chapter_type", nullable = false)
    private ChapterType chapterType;

    @Column(name = "chapter_num", nullable = false)
    private Integer chapterNum;

    @Enumerated(EnumType.STRING)
    @Column(name = "collect_book_type", nullable = false)
    private CollectBookType collectBookType;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "collectBook", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chapter> chapters = new ArrayList<>();

    @Builder
    public CollectBook(User user, String title, BookColor bookColor, Integer year,
                       Visibility visibility, ChapterType chapterType, Integer chapterNum,
                       CollectBookType collectBookType) {
        this.user = user;
        this.title = title;
        this.bookColor = bookColor;
        this.year = year;
        this.visibility = visibility != null ? visibility : Visibility.PUBLIC;
        this.chapterType = chapterType;
        this.chapterNum = chapterNum;
        this.collectBookType = collectBookType != null ? collectBookType : CollectBookType.CUSTOM;
    }

    public void addChapter(Chapter chapter) {
        this.chapters.add(chapter);
        chapter.assignCollectBook(this);
    }

    public void updateVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public boolean isSystemType() {
        return this.collectBookType == CollectBookType.SYSTEM;
    }
}