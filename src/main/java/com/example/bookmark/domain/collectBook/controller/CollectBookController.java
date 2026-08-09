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

    @Operation(
            summary = "콜렉트북 생성",
            description = "새로운 콜렉트북과 내부 챕터 목록(월 단위 12개 또는 직접 설정 1~20개)을 생성합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "콜렉트북 생성 성공",
                    content = @Content(schema = @Schema(implementation = CollectBookCreateResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (입력값 검증 실패 또는 챕터 개수 조건 미충족)",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<CollectBookCreateResponse> createCollectBook(
            @Valid @RequestBody CollectBookCreateRequest request
    ) {
        // TODO: 추후 Security/JWT 도입 시 인증된 유저의 PK로 대체
        Long tempUserId = 1L;

        CollectBookCreateResponse response = collectBookService.createCollectBook(tempUserId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}