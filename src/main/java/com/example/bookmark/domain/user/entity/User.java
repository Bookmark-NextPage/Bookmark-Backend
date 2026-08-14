package com.example.bookmark.domain.user.entity;

import com.example.bookmark.domain.bucketBoard.entity.BoardTheme;
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
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, length = 20)
    private String name; // 프로필에 있는 이름 - 변경 O

    @Column(name = "login_id", nullable = false, unique = true, length = 30)
    private String loginId; // 로그인 아이디 - 변경 X

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "bio", length = 100)
    private String bio; // 한줄소개

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_theme_id")
    private BoardTheme boardTheme;

    // 1. 자바 필드 기본값 true 설정 + DB default 설정 추가
    @Column(name = "ai_use", nullable = false, columnDefinition = "boolean default true")
    private Boolean aiUse = true;

    @Column(nullable = false)
    private Boolean isInAppNotificationEnabled = true; // 기본값 ON

    public void changeBoardTheme(BoardTheme boardTheme) {
        this.boardTheme = boardTheme;
    }

    // 2. Builder 생성자에 aiUse 파라미터를 받고, null일 경우 true가 들어가도록 처리
    @Builder
    public User(String name, String loginId, String email, String password, BoardTheme boardTheme, Boolean aiUse, Boolean isInAppNotificationEnabled) {
        this.name = name;
        this.loginId = loginId;
        this.email = email;
        this.password = password;
        this.boardTheme = boardTheme;
        this.aiUse = (aiUse != null) ? aiUse : true;
        this.isInAppNotificationEnabled = (isInAppNotificationEnabled != null) ? isInAppNotificationEnabled : true;
    }

    // 프로필 편집
    public void updateProfile(String name, String bio, String profileImageUrl) {
        this.name = name;
        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
    }

    // 설정 변경 메서드
    public void updateAiUse(Boolean aiUse) {
        this.aiUse = aiUse;
    }

    // 알림 on/off 변경 메서드
    public void updateInAppNotificationSetting(boolean enabled) {
        this.isInAppNotificationEnabled = enabled;
    }
}