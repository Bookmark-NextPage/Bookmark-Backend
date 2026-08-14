package com.example.bookmark.domain.user.service;

import com.example.bookmark.common.exception.CustomException;
import com.example.bookmark.domain.user.dto.request.LoginRequest;
import com.example.bookmark.domain.user.dto.response.LoginResponse;
import com.example.bookmark.domain.user.entity.User;
import com.example.bookmark.domain.user.exception.UserErrorCode;
import com.example.bookmark.domain.user.repository.UserRepository;
import com.example.bookmark.global.auth.JwtProvider;
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
    private final JwtProvider jwtProvider;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository
                .findByLoginIdOrEmail(request.identifier(), request.identifier())
                .orElseThrow(() -> new CustomException(UserErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new CustomException(UserErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = jwtProvider.createAccessToken(user.getId());
        return LoginResponse.of(user, accessToken);
    }
}