package com.example.bookmark.domain.collectBook.entity;

import com.example.bookmark.domain.collectBook.entity.enums.BookColor;
import com.example.bookmark.domain.collectBook.entity.enums.ChapterType;
import com.example.bookmark.domain.collectBook.entity.enums.Visibility;
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
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class) // @CreatedDate 작동에 필요
@Table(name = "collect_book")
public class CollectBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collect_book_id")
    private Long id;

    // TODO: 유저 연결 필요!
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "book_color", nullable = false)
    private BookColor bookColor;

    @Column(nullable = false)
    private Integer year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility;

    @Enumerated(EnumType.STRING)
    @Column(name = "chapter_type", nullable = false)
    private ChapterType chapterType;

    @Column(name = "chapter_num", nullable = false)
    private Integer chapterNum;

    @CreatedDate // 자동 생성 시간 할당
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "collectBook", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chapter> chapters = new ArrayList<>();

    @Builder
    public CollectBook(Long userId, String title, BookColor bookColor, Integer year,
                       Visibility visibility, ChapterType chapterType, Integer chapterNum) {
        this.userId = userId;
        this.title = title;
        this.bookColor = bookColor;
        this.year = year;
        this.visibility = (visibility != null) ? visibility : Visibility.PUBLIC;        this.chapterType = chapterType;
        this.chapterNum = chapterNum;
    }

    public void addChapter(Chapter chapter) {
        this.chapters.add(chapter);
        chapter.assignCollectBook(this);
    }
}