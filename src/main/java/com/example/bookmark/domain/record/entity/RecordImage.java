package com.example.bookmark.domain.record.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "record_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecordImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", nullable = false)
    private Record record;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "image_seq", nullable = false)
    private Integer imageSeq;

    @Builder
    public RecordImage(String imageUrl, Integer imageSeq) {
        this.imageUrl = imageUrl;
        this.imageSeq = imageSeq;
    }

    public void assignRecord(Record record) {
        this.record = record;
    }
}