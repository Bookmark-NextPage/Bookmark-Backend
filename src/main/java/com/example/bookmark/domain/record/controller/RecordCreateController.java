package com.example.bookmark.domain.record.controller;

import com.example.bookmark.common.response.ApiResponse;
import com.example.bookmark.domain.record.dto.request.RecordCreateRequest;
import com.example.bookmark.domain.record.dto.response.RecordSaveResponse;
import com.example.bookmark.domain.record.service.RecordService;
import com.example.bookmark.global.auth.LoginUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Record API", description = "콜렉트북 기록 API")
@RestController
@RequestMapping("/api/collect-books")
@RequiredArgsConstructor
public class RecordCreateController {

    private final RecordService recordService;

    @Operation(summary = "콜렉트북에 기록 생성 - 메모지 기반X", description = "콜렉트북 안의 챕터 내에서 기록 생성 버튼을 통해 최종 저장합니다.")
    @PostMapping("/chapters/{chapterId}/records")
    public ResponseEntity<ApiResponse<RecordSaveResponse>> createCustomRecord(
            @LoginUserId Long userId,
            @PathVariable Long chapterId,
            @Valid @RequestBody RecordCreateRequest request
    ) {
        RecordSaveResponse response = recordService.createCustomRecord(userId, chapterId, request);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "콜렉트북에 기록 생성 - 메모지 기반O", description = "버킷 보드에서 '이뤘어요' 버튼을 눌러 작성일 기준 월별 자동 생성 콜렉트북 챕터에 최종 저장합니다.")
    @PostMapping("/memos/{bucketBoardMemoId}/records")
    public ResponseEntity<ApiResponse<RecordSaveResponse>> createMemoRecord(
            @LoginUserId Long userId,
            @PathVariable Long bucketBoardMemoId,
            @Valid @RequestBody RecordCreateRequest request
    ) {
        RecordSaveResponse response = recordService.createMemoRecord(userId, bucketBoardMemoId, request);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}