package com.example.bookmark.domain.bucketBoard.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "memo_design")
public class MemoDesign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memo_design_id")
    private Long memoDesignId;

    @Column(name = "memo_image_url", nullable = false)
    private String memoImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_theme_id", nullable = false)
    private BoardTheme boardTheme;

    /*
     * 메모지 전체 크기를 100%로 봤을 때,
     * 내용(태그 + 텍스트)을 표시할 안전 영역의 비율값입니다.
     */
    @Column(name = "content_left", nullable = false)
    private Double contentLeft;

    @Column(name = "content_top", nullable = false)
    private Double contentTop;

    @Column(name = "content_width", nullable = false)
    private Double contentWidth;

    @Column(name = "content_height", nullable = false)
    private Double contentHeight;

}
