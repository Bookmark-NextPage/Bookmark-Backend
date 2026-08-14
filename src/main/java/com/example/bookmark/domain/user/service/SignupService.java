package com.example.bookmark.domain.user.service;

import com.example.bookmark.common.exception.CustomException;
import com.example.bookmark.domain.bucketBoard.entity.BoardTheme;
import com.example.bookmark.domain.bucketBoard.repository.BoardThemeRepository;
import com.example.bookmark.domain.collectBook.service.SystemCollectBookService;
import com.example.bookmark.domain.user.dto.request.SignupRequest;
import com.example.bookmark.domain.user.dto.response.SignupResponse;
import com.example.bookmark.domain.user.entity.User;
import com.example.bookmark.domain.user.exception.UserErrorCode;
import com.example.bookmark.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SignupService {

    private static final Long DEFAULT_BOARD_THEME_ID = 1L;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SystemCollectBookService systemCollectBookService;
    private final BoardThemeRepository boardThemeRepository;

    @Transactional
    public SignupResponse signup(SignupRequest request) {

        // 1. 비밀번호 / 비밀번호 확인 일치 검증
        if (!request.password().equals(request.passwordConfirm())) {
            throw new CustomException(UserErrorCode.PASSWORD_CONFIRM_MISMATCH);
        }

        // 2. 아이디 / 이메일 중복 검증
        if (userRepository.existsByLoginId(request.loginId())) {
            throw new CustomException(UserErrorCode.DUPLICATE_LOGIN_ID);
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new CustomException(UserErrorCode.DUPLICATE_EMAIL);
        }

        BoardTheme defaultTheme = boardThemeRepository.findById(DEFAULT_BOARD_THEME_ID)
                .orElseThrow(() -> new IllegalStateException("기본 보드 테마가 존재하지 않습니다. (id=1)"));

        // 3. 비밀번호 암호화 후 저장
        User user = User.builder()
                .name(request.name())
                .loginId(request.loginId())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .boardTheme(defaultTheme)
                .aiUse(true)
                .isInAppNotificationEnabled(true)
                .build();

        User savedUser = userRepository.save(user);

        // 4. 가입 연도 기준 시스템 콜렉트북 자동 생성
        int currentYear = LocalDate.now().getYear();
        systemCollectBookService.createSystemCollectBook(savedUser, currentYear);

        return SignupResponse.from(savedUser);
    }
}