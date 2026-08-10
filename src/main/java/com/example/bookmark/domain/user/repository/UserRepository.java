package com.example.bookmark.domain.user.repository;

import com.example.bookmark.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByLoginId(String loginId);

    boolean existsByEmail(String email);

    // 로그인 시 "아이디 또는 이메일" 한 칸으로 조회
    Optional<User> findByLoginIdOrEmail(String loginId, String email);

    // 이름(부분 일치)으로 유저 검색
    List<User> findByNameContainingIgnoreCase(String name);
}