package com.example.bookmark.domain.user.controller;

import com.example.bookmark.domain.user.dto.request.LoginRequest;
import com.example.bookmark.domain.user.dto.request.ProfileUpdateRequest;
import com.example.bookmark.domain.user.dto.request.SignupRequest;
import com.example.bookmark.domain.user.dto.response.LoginResponse;
import com.example.bookmark.domain.user.dto.response.ProfileResponse;
import com.example.bookmark.domain.user.dto.response.SignupResponse;
import com.example.bookmark.domain.user.service.LoginService;
import com.example.bookmark.domain.user.service.ProfileService;
import com.example.bookmark.domain.user.service.SignupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "회원 API")
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final SignupService signupService;
    private final LoginService loginService;
    private final ProfileService profileService;
    // TODO: Security/JWT 도입 시 인증 유저의 PK로 대체
    private static final Long TEMP_USER_ID = 1L;

    @Operation(summary = "회원가입", description = "이름, 아이디, 이메일, 비밀번호로 신규 회원을 등록합니다.")
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        SignupResponse response = signupService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "로그인", description = "아이디 또는 이메일과 비밀번호로 로그인합니다.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = loginService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "프로필 편집", description = "프로필 사진, 이름, 한줄소개를 수정합니다.")
    @PatchMapping("/me/profile")
    public ResponseEntity<ProfileResponse> updateProfile(
            @Valid @RequestBody ProfileUpdateRequest request
    ) {
        ProfileResponse response = profileService.updateProfile(TEMP_USER_ID, request);
        return ResponseEntity.ok(response);
    }
}