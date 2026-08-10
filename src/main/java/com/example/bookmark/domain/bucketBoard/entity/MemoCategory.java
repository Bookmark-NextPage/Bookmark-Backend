package com.example.bookmark.domain.bucketBoard.entity;

import com.example.bookmark.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "memo_category")
public class MemoCategory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memo_category_id")
    private Long MemoCategoryId;

    // TODO: 유저 연결 필요!
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "category_name", nullable = false, length = 20)
    private String categoryName;

    @Column(name = "default_category", nullable = false)
    private Boolean defaultCategory;

}
