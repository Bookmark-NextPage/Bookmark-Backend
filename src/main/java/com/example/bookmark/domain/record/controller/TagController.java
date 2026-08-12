package com.example.bookmark.domain.record.controller;

import com.example.bookmark.common.response.ApiResponse;
import com.example.bookmark.domain.record.dto.request.TagCreateRequest;
import com.example.bookmark.domain.record.dto.response.TagResponse;
import com.example.bookmark.domain.record.service.TagService;
import com.example.bookmark.global.auth.LoginUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Record API", description = "콜렉트북 기록 API")
@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @Operation(summary = "태그 생성", description = "기록 작성 시 사용할 사용자 정의 감성 태그를 생성합니다. (최대 10개 제한)")
    @PostMapping
    public ResponseEntity<ApiResponse<TagResponse>> createTag(
            @LoginUserId Long userId,
            @Valid @RequestBody TagCreateRequest request
    ) {
        TagResponse response = tagService.createTag(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @Operation(summary = "내 태그 목록 조회", description = "사용자가 생성한 감성 키워드(태그) 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TagResponse>>> getMyTags(
            @LoginUserId Long userId
    ) {
        List<TagResponse> response = tagService.getMyTags(userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}