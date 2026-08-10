package com.example.bookmark.domain.bucketBoard.entity;

import com.example.bookmark.domain.bucketBoard.entity.enums.MemoState;
import com.example.bookmark.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "bucket_board_memo")
public class BucketBoardMemo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bucket_board_memo_id")
    private Long BucketBoardMemoId;

    // TODO: 유저 연결 필요!
    @Column(name = "user_id", nullable = false)
    private Long userId;

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
