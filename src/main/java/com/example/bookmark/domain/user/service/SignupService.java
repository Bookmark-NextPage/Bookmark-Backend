package com.example.bookmark.domain.user.service;

import com.example.bookmark.domain.bucketBoard.entity.BucketBoardMemo;
import com.example.bookmark.domain.user.dto.request.SignupRequest;
import com.example.bookmark.domain.user.dto.response.SignupResponse;
import com.example.bookmark.domain.user.entity.User;
import com.example.bookmark.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SignupService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignupResponse signup(SignupRequest request) {

        // 1. 비밀번호 / 비밀번호 확인 일치 검증
        if (!request.password().equals(request.passwordConfirm())) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        // 2. 아이디 / 이메일 중복 검증
        if (userRepository.existsByLoginId(request.loginId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 3. 비밀번호 암호화 후 저장
        User user = User.builder()
                .name(request.name())
                .loginId(request.loginId())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        User savedUser = userRepository.save(user);

        return SignupResponse.from(savedUser);
    }
}