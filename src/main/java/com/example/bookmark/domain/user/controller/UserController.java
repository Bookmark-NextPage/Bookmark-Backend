package com.example.bookmark.domain.user.controller;

import com.example.bookmark.domain.user.dto.request.LoginRequest;
import com.example.bookmark.domain.user.dto.request.ProfileUpdateRequest;
import com.example.bookmark.domain.user.dto.request.SignupRequest;
import com.example.bookmark.domain.user.dto.request.WithdrawRequest;
import com.example.bookmark.domain.user.dto.response.LoginResponse;
import com.example.bookmark.domain.user.dto.response.MyPageResponse;
import com.example.bookmark.domain.user.dto.response.ProfileResponse;
import com.example.bookmark.domain.user.dto.response.SignupResponse;
import com.example.bookmark.domain.user.service.*;
import com.example.bookmark.global.auth.LoginUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "회원 API")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final SignupService signupService;
    private final LoginService loginService;
    private final ProfileService profileService;
    private final MyPageService myPageService;
    private final AccountService accountService;

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

    @Operation(summary = "친구 마이페이지 조회", description = "userId로 해당 유저의 마이페이지를 조회합니다. 책 목록은 조회자와의 관계에 따라 공개 범위가 필터링됩니다.")
    @GetMapping("/{userId}/mypage")
    public ResponseEntity<MyPageResponse> getMyPage(
            @LoginUserId Long viewerId,
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(myPageService.getMyPage(viewerId, userId));
    }

    @Operation(summary = "내 마이페이지 조회", description = "로그인한 본인의 마이페이지를 조회합니다.")
    @GetMapping("/me/mypage")
    public ResponseEntity<MyPageResponse> getMyMyPage(@LoginUserId Long meId) {
        return ResponseEntity.ok(myPageService.getMyPage(meId, meId));
    }

    @Operation(summary = "프로필 편집", description = "프로필 사진, 이름, 한줄소개를 수정합니다.")
    @PatchMapping("/me/profile")
    public ResponseEntity<ProfileResponse> updateProfile(
            @LoginUserId Long userId,
            @Valid @RequestBody ProfileUpdateRequest request
    ) {
        return ResponseEntity.ok(profileService.updateProfile(userId, request));
    }

    @Operation(summary = "로그아웃", description = "로그아웃합니다. (accessToken 방식이라 클라이언트가 토큰을 삭제)")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@LoginUserId Long userId) {
        accountService.logout(userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 탈퇴", description = "비밀번호 재확인 후 회원 정보와 연관 데이터를 삭제합니다.")
    @DeleteMapping("/me")
    public ResponseEntity<Void> withdraw(
            @LoginUserId Long userId,
            @Valid @RequestBody WithdrawRequest request
    ) {
        accountService.withdraw(userId, request.password());
        return ResponseEntity.noContent().build();
    }
}