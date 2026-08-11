package com.example.bookmark.domain.record.controller;

import com.example.bookmark.common.response.ApiResponse;
import com.example.bookmark.domain.record.dto.request.AiImageGenerateRequest;
import com.example.bookmark.domain.record.dto.response.AiImageGenerateResponse;
import com.example.bookmark.domain.record.service.AiRecordService;
import com.example.bookmark.global.auth.LoginUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Record API", description = "콜렉트북 기록 API")
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class RecordAiController {

    private final AiRecordService aiRecordService;

    @Operation(summary = "AI 추천 감성 스크랩북 이미지 생성/재생성")
    @PostMapping("/scrap-image")
    public ResponseEntity<ApiResponse<AiImageGenerateResponse>> generateScrapImage(
            @LoginUserId Long userId,
            @Valid @RequestBody AiImageGenerateRequest request
    ) {
        AiImageGenerateResponse response = aiRecordService.generateScrapImage(userId, request);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}