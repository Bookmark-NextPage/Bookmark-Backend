package com.example.bookmark.domain.collectBook.controller;


import com.example.bookmark.domain.collectBook.dto.request.CollectBookCreateRequest;
import com.example.bookmark.domain.collectBook.dto.response.CollectBookCreateResponse;
import com.example.bookmark.domain.collectBook.service.CollectBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "CollectBook", description = "콜렉트북 API")
@RestController
@RequestMapping("/api/v1/collect-books")
@RequiredArgsConstructor
public class CollectBookController {

    private final CollectBookService collectBookService;

    // 콜렉트 북 생성
    @Operation(
            summary = "콜렉트북 생성",
            description = "새로운 콜렉트북과 내부 챕터 목록(월 단위 12개 또는 직접 설정 1~20개)을 생성합니다."
    )
    @PostMapping
    public ResponseEntity<CollectBookCreateResponse> createCollectBook(
            @Valid @RequestBody CollectBookCreateRequest request
    ) {
        // TODO: 추후 Security/JWT 도입 시 인증된 유저의 PK로 대체
        Long tempUserId = 1L;

        CollectBookCreateResponse response = collectBookService.createCollectBook(tempUserId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 콜렉트 북 삭제
    @Operation(
            summary = "콜렉트북 삭제",
            description = "콜렉트북과 그 내부에 포함된 모든 챕터를 삭제합니다."
    )
    @DeleteMapping("/{collectBookId}")
    public ResponseEntity<Void> deleteCollectBook(
            @PathVariable Long collectBookId
    ) {
        // TODO: 추후 Security/JWT 도입 시 인증된 유저의 PK로 대체
        Long tempUserId = 1L;

        collectBookService.deleteCollectBook(tempUserId, collectBookId);

        return ResponseEntity.noContent().build();
    }
}