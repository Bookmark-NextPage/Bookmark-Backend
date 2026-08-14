package com.example.bookmark.domain.bucketBoard.controller;

import com.example.bookmark.domain.bucketBoard.dto.request.BucketMoveRequest;
import com.example.bookmark.domain.bucketBoard.dto.request.BucketWriteRequest;
import com.example.bookmark.domain.bucketBoard.dto.request.CategoryRequest;
import com.example.bookmark.domain.bucketBoard.dto.response.*;
import com.example.bookmark.domain.bucketBoard.service.BucketBoardService;
import com.example.bookmark.global.auth.LoginUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "BucketBoard", description = "버킷보드 API")
@RestController
@RequestMapping("/api/bucketBoard")
@RequiredArgsConstructor
public class BucketBoardController {

    private final BucketBoardService bucketBoardService;

    @Operation(
            summary = "버킷보드 가져오기",
            description = ""
    )
    @GetMapping("")
    public ResponseEntity<BoardResponse> getBoard(
            @RequestParam(required = false) Long categoryId,
            @LoginUserId Long userId
    ){
        BoardResponse boardResponse = bucketBoardService.getBoard(categoryId, userId);
        return ResponseEntity.ok(boardResponse);

    }

    @Operation(
            summary = "버킷 작성하기",
            description = ""
    )
    @PostMapping("/createBucket")
    public ResponseEntity<MemoResponse> createBucket(
            @LoginUserId Long userId,
            @RequestBody BucketWriteRequest request
    ){
        MemoResponse response = bucketBoardService.createBucket(userId, request);
        return ResponseEntity.ok(response);

    }

    @Operation(
            summary = "버킷 수정하기",
            description = ""
    )
    @PutMapping("/updateBucket/{bucketId}")
    public ResponseEntity<MemoResponse> updateBucket(
            @LoginUserId Long userId,
            @RequestBody BucketWriteRequest request,
            @RequestParam Long bucketId
    ){
        MemoResponse response = bucketBoardService.updateBucket(userId, bucketId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "버킷 삭제하기",
            description = ""
    )
    @DeleteMapping("/deleteBucket/{bucketId}")
    public ResponseEntity<?> deleteBucket(
            @LoginUserId Long userId,
            @RequestParam Long bucketId
    ){
        Long result = bucketBoardService.deleteBucket(userId, bucketId);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "버킷 완료하기",
            description = ""
    )
    @PatchMapping("/completeBucket/{bucketId}")
    public ResponseEntity<?> completeBucket(
            @LoginUserId Long userId,
            @RequestParam Long bucketId
    ){
        Long result = bucketBoardService.completeBucket(userId, bucketId);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "버킷 이동하기",
            description = ""
    )
    @PatchMapping("/moveBucket/{bucketId}")
    public ResponseEntity<BucketMoveResponse> moveBucket(
            @LoginUserId Long userId,
            @RequestParam Long bucketId,
            @RequestBody BucketMoveRequest request
    ){
        BucketMoveResponse response = bucketBoardService.moveBucket(userId, bucketId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "카테고리 가져오기",
            description = ""
    )
    @GetMapping("/getCategory")
    public ResponseEntity<List<CategoryResponse>> getCategory(
            @LoginUserId Long userId
    ){
        List<CategoryResponse> response = bucketBoardService.getCategory(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "카테고리 추가하기",
            description = ""
    )
    @PostMapping("/createCategory")
    public ResponseEntity<CategoryResponse> createCategory(
            @LoginUserId Long userId,
            @RequestBody CategoryRequest request
    ){
        CategoryResponse response = bucketBoardService.createCategory(userId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "보드 테마 목록 가져오기",
            description = ""
    )
    @GetMapping("/getBoardThemes")
    public ResponseEntity<BoardThemesResponse> getBoardThemes(
            @LoginUserId Long userId
    ){
        BoardThemesResponse response = bucketBoardService.getBoardThemes(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "보드 테마 선택하기",
            description = ""
    )
    @PatchMapping("/selectBoardThemes/{boardThemeId}")
    public ResponseEntity<?> selectBoardThemes(
            @LoginUserId Long userId,
            @RequestParam Long boardThemeId
    ){
        bucketBoardService.selectBoardThemes(userId, boardThemeId);
        return ResponseEntity.ok("테마가 성공적으로 변경되었습니다.");
    }



}
