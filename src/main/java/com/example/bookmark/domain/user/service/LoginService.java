package com.example.bookmark.domain.user.service;

import com.example.bookmark.domain.user.dto.request.LoginRequest;
import com.example.bookmark.domain.user.dto.response.LoginResponse;
import com.example.bookmark.domain.user.entity.User;
import com.example.bookmark.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {

        // 1. 아이디 또는 이메일로 유저 조회
        User user = userRepository
                .findByLoginIdOrEmail(request.identifier(), request.identifier())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        // 2. 비밀번호 검증 (평문 vs 암호화된 값 비교)
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // TODO: JWT 도입 시 여기서 accessToken 발급해 응답에 포함
        return LoginResponse.from(user);
    }
}