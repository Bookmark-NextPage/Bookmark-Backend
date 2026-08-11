package com.example.bookmark.domain.bucketBoard.controller;

import com.example.bookmark.domain.bucketBoard.dto.request.BucketWriteRequest;
import com.example.bookmark.domain.bucketBoard.dto.response.BoardResponse;
import com.example.bookmark.domain.bucketBoard.dto.response.MemoResponse;
import com.example.bookmark.domain.bucketBoard.entity.BucketBoardMemo;
import com.example.bookmark.domain.bucketBoard.service.BucketBoardService;
import com.example.bookmark.global.auth.LoginUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PutMapping("/updateBucket")
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
    @DeleteMapping("/deleteBucket")
    public ResponseEntity deleteBucket(
            @LoginUserId Long userId,
            @RequestParam Long bucketId
    ){
        Long result = bucketBoardService.deleteBucket(userId, bucketId);
        return ResponseEntity.ok(result);
    }

}
