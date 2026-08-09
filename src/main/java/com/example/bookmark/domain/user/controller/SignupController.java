package com.example.bookmark.domain.user.controller;

import com.example.bookmark.domain.user.dto.request.SignupRequest;
import com.example.bookmark.domain.user.dto.response.SignupResponse;
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
public class SignupController {

    private final SignupService signupService;

    @Operation(summary = "회원가입", description = "이름, 아이디, 이메일, 비밀번호로 신규 회원을 등록합니다.")
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        SignupResponse response = signupService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}