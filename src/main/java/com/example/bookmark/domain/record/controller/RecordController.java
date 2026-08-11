package com.example.bookmark.domain.record.controller;

import com.example.bookmark.domain.record.dto.request.CommentCreateRequest;
import com.example.bookmark.domain.record.dto.request.RecordCreateRequest;
import com.example.bookmark.domain.record.dto.request.RecordDraftSaveRequest;
import com.example.bookmark.domain.record.dto.response.RecordDetailResponse;
import com.example.bookmark.domain.record.dto.response.RecordSaveResponse;
import com.example.bookmark.domain.record.service.RecordService;
import com.example.bookmark.global.auth.LoginUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Record API", description = "콜렉트북 기록 및 임시저장 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @Operation(summary = "기록 임시 저장", description = "입력 값이 완벽하지 않아도 작성 중인 내용을 임시 저장합니다.")
    @PostMapping("/records")
    public ResponseEntity<RecordSaveResponse> saveDraft(
            @LoginUserId Long userId,
            @RequestBody RecordDraftSaveRequest request
    ) {
        return ResponseEntity.ok(recordService.saveDraft(userId, request));
    }

    @Operation(summary = "최근 임시 저장 조회", description = "가장 최근에 작성하다가 임시 저장된 기록 1건을 조회합니다.")
    @GetMapping("/records/drafts/latest")
    public ResponseEntity<RecordSaveResponse> getLatestDraft(
            @LoginUserId Long userId
    ) {
        return ResponseEntity.ok(recordService.getLatestDraft(userId));
    }

    @Operation(summary = "콜렉트북에 기록 생성 - 메모지 기반X", description = "콜렉트북 안의 챕터 내에서 기록 생성 버튼을 통해 최종 저장합니다.")
    @PostMapping("/collect-books/{collectBookId}/chapters/{chapterId}/records")
    public ResponseEntity<RecordSaveResponse> createCustomRecord(
            @LoginUserId Long userId,
            @PathVariable Long collectBookId,
            @PathVariable Long chapterId,
            @Valid @RequestBody RecordCreateRequest request
    ) {
        return ResponseEntity.ok(recordService.createCustomRecord(userId, collectBookId, chapterId, request));
    }

    @Operation(summary = "콜렉트북에 기록 생성 - 메모지 기반O", description = "버킷 보드에서 '이뤘어요' 버튼을 눌러 작성일 기준 월별 자동 생성 콜렉트북 챕터에 최종 저장합니다.")
    @PostMapping("/collect-books/memos/{memoId}/records")
    public ResponseEntity<RecordSaveResponse> createMemoRecord(
            @LoginUserId Long userId,
            @PathVariable Long memoId,
            @Valid @RequestBody RecordCreateRequest request
    ) {
        return ResponseEntity.ok(recordService.createMemoRecord(userId, memoId, request));
    }

    @Operation(
            summary = "콜렉트북 기록 상세 조회",
            description = "콜렉트북 스크랩북 형태의 상세 화면입니다. 본문, 키워드, AI 스크랩북 이미지, 좋아요 수/여부, 댓글 목록을 포함합니다."
    )
    @GetMapping("/collect-books/{collectBookId}/records/{recordId}")
    public ResponseEntity<RecordDetailResponse> getRecordDetail(
            @LoginUserId Long userId,
            @PathVariable Long collectBookId,
            @PathVariable Long recordId
    ) {
        return ResponseEntity.ok(recordService.getRecordDetail(userId, collectBookId, recordId));
    }

    @Operation(summary = "댓글 작성", description = "친구인 유저들끼리 서로의 콜렉트북 내 기록에 댓글을 남깁니다.")
    @PostMapping("/collect-books/{collectBookId}/records/{recordId}/comments")
    public ResponseEntity<Void> createComment(
            @LoginUserId Long userId,
            @PathVariable Long collectBookId,
            @PathVariable Long recordId,
            @Valid @RequestBody CommentCreateRequest request
    ) {
        recordService.createComment(userId, collectBookId, recordId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "좋아요 등록", description = "친구인 유저들끼리 서로의 콜렉트북 내 기록에 공감(좋아요)을 남깁니다.")
    @PostMapping("/collect-books/{collectBookId}/records/{recordId}/likes")
    public ResponseEntity<Void> addLike(
            @LoginUserId Long userId,
            @PathVariable Long collectBookId,
            @PathVariable Long recordId
    ) {
        recordService.addLike(userId, collectBookId, recordId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "좋아요 삭제", description = "남겼던 공감(좋아요)을 취소합니다.")
    @DeleteMapping("/collect-books/{collectBookId}/records/{recordId}/likes")
    public ResponseEntity<Void> deleteLike(
            @LoginUserId Long userId,
            @PathVariable Long collectBookId,
            @PathVariable Long recordId
    ) {
        recordService.deleteLike(userId, collectBookId, recordId);
        return ResponseEntity.noContent().build();
    }
}