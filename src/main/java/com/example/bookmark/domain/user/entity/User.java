package com.example.bookmark.domain.user.entity;

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

    @Builder
    public User(String name, String loginId, String email, String password) {
        this.name = name;
        this.loginId = loginId;
        this.email = email;
        this.password = password;
    }

    // 프로필 편집
    public void updateProfile(String name, String bio, String profileImageUrl) {
        this.name = name;
        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
    }
}