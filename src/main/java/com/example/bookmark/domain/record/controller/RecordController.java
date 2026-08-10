package com.example.bookmark.domain.record.controller;

import com.example.bookmark.domain.record.dto.request.RecordCreateRequest;
import com.example.bookmark.domain.record.dto.request.RecordDraftSaveRequest;
import com.example.bookmark.domain.record.dto.response.RecordSaveResponse;
import com.example.bookmark.domain.record.service.RecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Record API", description = "콜렉트북 기록 및 임시저장 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @Operation(summary = "기록 임시 저장", description = "입력 값이 완벽하지 않아도 작성 중인 내용을 임시 저장합니다.")
    @PostMapping("/records")
    public ResponseEntity<RecordSaveResponse> saveDraft(
            @RequestBody RecordDraftSaveRequest request
    ) {
        return ResponseEntity.ok(recordService.saveDraft(request));
    }

    @Operation(summary = "최근 임시 저장 조회", description = "가장 최근에 작성하다가 임시 저장된 기록 1건을 조회합니다.")
    @GetMapping("/records/drafts/latest")
    public ResponseEntity<RecordSaveResponse> getLatestDraft() {
        return ResponseEntity.ok(recordService.getLatestDraft());
    }

    @Operation(summary = "콜렉트북에 기록 생성 - 메모지 기반X", description = "콜렉트북 안의 챕터 내에서 기록 생성 버튼을 통해 최종 저장합니다.")
    @PostMapping("/collect-books/{collectBookId}/chapters/{chapterId}/records")
    public ResponseEntity<RecordSaveResponse> createCustomRecord(
            @PathVariable Long collectBookId,
            @PathVariable Long chapterId,
            @Valid @RequestBody RecordCreateRequest request
    ) {
        return ResponseEntity.ok(recordService.createCustomRecord(collectBookId, chapterId, request));
    }

    // TODO : 메모 연결 필요 !
    @Operation(summary = "콜렉트북에 기록 생성 - 메모지 기반O", description = "버킷 보드에서 '이뤘어요' 버튼을 눌러 작성일 기준 월별 자동 생성 콜렉트북 챕터에 최종 저장합니다.")
    @PostMapping("/collect-books/memos/{memoId}/records")
    public ResponseEntity<RecordSaveResponse> createMemoRecord(
            @PathVariable Long memoId,
            @Valid @RequestBody RecordCreateRequest request
    ) {
        return ResponseEntity.ok(recordService.createMemoRecord(memoId, request));
    }
}