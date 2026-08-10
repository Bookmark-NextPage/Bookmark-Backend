package com.example.bookmark.domain.collectBook.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chapter")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chapter_id")
    private Long id;

    @Column(nullable = false, length = 10)
    private String name;

    @Column(nullable = false)
    private Integer sequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collect_book_id", nullable = false)
    private CollectBook collectBook;

    @Builder
    public Chapter(String name, Integer sequence) {
        this.name = name;
        this.sequence = sequence;
    }

    public void assignCollectBook(CollectBook collectBook) {
        this.collectBook = collectBook;
    }
}