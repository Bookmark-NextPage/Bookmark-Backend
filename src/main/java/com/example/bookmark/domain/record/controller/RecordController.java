package com.example.bookmark.domain.record.controller;

import com.example.bookmark.common.response.ApiResponse;
import com.example.bookmark.domain.record.dto.request.CommentCreateRequest;
import com.example.bookmark.domain.record.dto.request.RecordAiImageUpdateRequest;
import com.example.bookmark.domain.record.dto.response.RecordDetailResponse;
import com.example.bookmark.domain.record.dto.response.RecordSearchResponse;
import com.example.bookmark.domain.record.service.RecordService;
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
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @Operation(
            summary = "콜렉트북 기록 상세 조회",
            description = "콜렉트북 스크랩북 형태의 상세 화면입니다. 본문, 키워드, AI 스크랩북 이미지, 좋아요 수/여부, 댓글 목록을 포함합니다."
    )
    @GetMapping("/{recordId}")
    public ResponseEntity<ApiResponse<RecordDetailResponse>> getRecordDetail(
            @LoginUserId Long userId,
            @PathVariable Long recordId
    ) {
        RecordDetailResponse response = recordService.getRecordDetail(userId, recordId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "댓글 작성", description = "친구인 유저들끼리 서로의 콜렉트북 내 기록에 댓글을 남깁니다.")
    @PostMapping("/{recordId}/comments")
    public ResponseEntity<ApiResponse<Void>> createComment(
            @LoginUserId Long userId,
            @PathVariable Long recordId,
            @Valid @RequestBody CommentCreateRequest request
    ) {
        recordService.createComment(userId, recordId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.onSuccess(null));
    }

    @Operation(summary = "좋아요 등록", description = "친구인 유저들끼리 서로의 콜렉트북 내 기록에 공감(좋아요)을 남깁니다.")
    @PostMapping("/{recordId}/likes")
    public ResponseEntity<ApiResponse<Void>> addLike(
            @LoginUserId Long userId,
            @PathVariable Long recordId
    ) {
        recordService.addLike(userId, recordId);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Operation(summary = "좋아요 삭제", description = "남겼던 공감(좋아요)을 취소합니다.")
    @DeleteMapping("/{recordId}/likes")
    public ResponseEntity<ApiResponse<Void>> deleteLike(
            @LoginUserId Long userId,
            @PathVariable Long recordId
    ) {
        recordService.deleteLike(userId, recordId);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Operation(
            summary = "AI 이미지 생성 후 최종 저장",
            description = "생성/재생성된 AI 추천 감성 스크랩북 이미지 URL을 해당 기록에 최종 반영(저장)합니다."
    )
    @PatchMapping("/{recordId}/ai-image/save")
    public ResponseEntity<ApiResponse<Void>> updateAiImageUrl(
            @LoginUserId Long userId,
            @PathVariable Long recordId,
            @Valid @RequestBody RecordAiImageUpdateRequest request
    ) {
        recordService.updateAiImageUrl(userId, recordId, request.getAiImageUrl());
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @GetMapping("/search")
    public ResponseEntity<List<RecordSearchResponse>> search(
            @LoginUserId Long userId,
            @RequestParam(required = false) String keyword
    ) {
        return ResponseEntity.ok(
                recordService.search(userId, keyword)
        );
    }

}