package com.example.bookmark.domain.bucketBoard.entity;

import com.example.bookmark.domain.bucketBoard.entity.enums.MemoState;
import com.example.bookmark.common.entity.BaseTimeEntity;
import com.example.bookmark.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "bucket_board_memo")
public class BucketBoardMemo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bucket_board_memo_id")
    private Long bucketBoardMemoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true) // nullable: 기본 카테고리는 null
    private User user;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "x_pos", nullable = false)
    private Double x;

    @Column(name = "y_pos", nullable = false)
    private Double y;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private MemoState state;

    @Column(name = "scrap_book", nullable = false)
    private Boolean scrapBook;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memo_design_id", nullable = false)
    private MemoDesign memoDesign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memo_category_id", nullable = false)
    private MemoCategory memoCategory;

}
