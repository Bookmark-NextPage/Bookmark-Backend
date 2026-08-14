package com.example.bookmark.domain.record.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "record_keywords")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecordKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_keyword_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", nullable = false)
    private Record record;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id", nullable = false)
    private Keyword keyword;

    @Builder
    public RecordKeyword(Record record, Keyword keyword) {
        this.record = record;
        this.keyword = keyword;
    }

    public void assignRecord(Record record) {
        this.record = record;
    }
}