package com.example.bookmark.domain.notification.entity;

import com.example.bookmark.domain.notification.entity.enums.NotificationType;
import com.example.bookmark.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver; // 알림을 받는 사용자

    @Enumerated(EnumType.STRING)
    private NotificationType type; // FRIEND_REQUEST, LIKE, COMMENT, SYSTEM

    private String senderName;  // 예: "유진", "현"
    private String content;     // 예: "회원님의 앱스토어 첫 출시 기록에 댓글..."
    private String redirectUrl; // 클릭 시 이동할 URL

    private boolean isRead = false; // 읽음 여부

    @CreatedDate
    private LocalDateTime createdAt;

    public void markAsRead() {
        this.isRead = true;
    }
}
