package com.example.bookmark.domain.bucketBoard.entity;

import com.example.bookmark.common.entity.BaseTimeEntity;
import com.example.bookmark.domain.bucketBoard.entity.enums.MemoState;
import com.example.bookmark.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "memo_category")
public class MemoCategory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memo_category_id")
    private Long memoCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // nullable: 기본 카테고리는 null
    private User user;

    @Column(name = "category_name", nullable = false, length = 20)
    private String categoryName;

    @Column(name = "default_category", nullable = false)
    private Boolean defaultCategory;

    @Builder
    public MemoCategory(
            User user,
            String categoryName
    ) {
        this.user = user;
        this.categoryName = categoryName;
        this.defaultCategory = false;
    }

}
