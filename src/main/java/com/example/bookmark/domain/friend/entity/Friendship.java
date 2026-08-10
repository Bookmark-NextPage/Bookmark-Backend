package com.example.bookmark.domain.friend.entity;

import com.example.bookmark.domain.friend.entity.enums.FriendStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "friendship",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_friend",
                columnNames = {"user_id", "friend_user_id"}
        )
)
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friendship_id")
    private Long id;

    // 신청 보낸 사람의 userId
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 신청 받은 사람의 userId
    @Column(name = "friend_user_id", nullable = false)
    private Long friendUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendStatus status;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Friendship(Long userId, Long friendUserId) {
        this.userId = userId;
        this.friendUserId = friendUserId;
        this.status = FriendStatus.PENDING; // 생성 시 신청 대기
    }

    public void accept() {
        this.status = FriendStatus.ACCEPTED;
    }
}