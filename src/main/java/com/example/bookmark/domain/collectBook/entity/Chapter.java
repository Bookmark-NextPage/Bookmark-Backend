package com.example.bookmark.domain.collectBook.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chapter")
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chapter_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collect_book_id", nullable = false)
    private CollectBook collectBook;

    @Column(nullable = false, length = 10)
    private String name; // ERD상 name 컬럼 (title -> name으로 맞춤)

    @Column(nullable = false)
    private Integer sequence;

    @Builder
    public Chapter(String name, Integer sequence) {
        this.name = name;
        this.sequence = sequence;
    }

    public void assignCollectBook(CollectBook collectBook) {
        this.collectBook = collectBook;
    }
}