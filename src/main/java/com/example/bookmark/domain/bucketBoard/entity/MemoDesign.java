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

}
