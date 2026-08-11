package com.example.bookmark.domain.collectBook.controller;

import com.example.bookmark.common.response.ApiResponse;
import com.example.bookmark.domain.collectBook.dto.request.CollectBookCreateRequest;
import com.example.bookmark.domain.collectBook.dto.request.CollectBookVisibilityUpdateRequest;
import com.example.bookmark.domain.collectBook.dto.response.CollectBookCreateResponse;
import com.example.bookmark.domain.collectBook.dto.response.CollectBookDetailResponse;
import com.example.bookmark.domain.collectBook.dto.response.CollectBookListResponse;
import com.example.bookmark.domain.collectBook.service.CollectBookService;
import com.example.bookmark.global.auth.LoginUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "CollectBook", description = "콜렉트북 API")
@RestController
@RequestMapping("/api/collect-books")
@RequiredArgsConstructor
public class CollectBookController {

    private final CollectBookService collectBookService;

    // 콜렉트 북 생성
    @Operation(
            summary = "콜렉트북 생성",
            description = "새로운 콜렉트북과 내부 챕터 목록(월 단위 12개 또는 직접 설정 1~20개)을 생성합니다."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<CollectBookCreateResponse>> createCollectBook(
            @LoginUserId Long userId,
            @Valid @RequestBody CollectBookCreateRequest request
    ) {
        CollectBookCreateResponse response = collectBookService.createCollectBook(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @Operation(
            summary = "콜렉트북 목록 조회",
            description = "사용자의 책장에 꽂힌 전체 콜렉트북 목록을 연도 내림차순으로 조회합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "목록 조회 성공")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CollectBookListResponse>>> getCollectBooks(
            @LoginUserId Long userId
    ) {
        List<CollectBookListResponse> response = collectBookService.getCollectBooks(userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(
            summary = "콜렉트북 상세 조회",
            description = "선택한 콜렉트북의 기본 정보와 내부 챕터 목록을 상세 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상세 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 콜렉트북 ID", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "조회 권한 없음", content = @Content)
    })
    @GetMapping("/{collectBookId}")
    public ResponseEntity<ApiResponse<CollectBookDetailResponse>> getCollectBookDetail(
            @LoginUserId Long userId,
            @PathVariable Long collectBookId
    ) {
        CollectBookDetailResponse response = collectBookService.getCollectBookDetail(userId, collectBookId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    // 콜렉트 북 삭제
    @Operation(
            summary = "콜렉트북 삭제",
            description = "콜렉트북과 그 내부에 포함된 모든 챕터를 삭제합니다."
    )
    @DeleteMapping("/{collectBookId}")
    public ResponseEntity<ApiResponse<Void>> deleteCollectBook(
            @LoginUserId Long userId,
            @PathVariable Long collectBookId
    ) {
        collectBookService.deleteCollectBook(userId, collectBookId);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Operation(summary = "콜렉트북 공개 범위 수정", description = "콜렉트북의 공개 범위(PUBLIC, FRIENDS, PRIVATE)를 변경합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "공개 범위 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "수정 권한 없음", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 콜렉트북 ID", content = @Content)
    })
    @PatchMapping("/{collectBookId}/visibility")
    public ResponseEntity<ApiResponse<Void>> updateVisibility(
            @LoginUserId Long userId,
            @PathVariable Long collectBookId,
            @Valid @RequestBody CollectBookVisibilityUpdateRequest request
    ) {
        collectBookService.updateVisibility(userId, collectBookId, request);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }
}