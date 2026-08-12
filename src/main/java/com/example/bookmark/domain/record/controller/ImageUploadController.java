package com.example.bookmark.domain.record.controller;

import com.example.bookmark.common.response.ApiResponse;
import com.example.bookmark.global.service.S3ImageUploadService; // 👈 S3 서비스로 교체
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Record API", description = "콜렉트북 기록 API")
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageUploadController {

    private final S3ImageUploadService s3ImageUploadService;

    @Operation(summary = "기록용 로컬 이미지 파일 업로드 (최대 5장)")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<String>>> uploadImages(
            @RequestPart("files") List<MultipartFile> files
    ) {
        List<String> imageUrls = s3ImageUploadService.uploadImages(files);
        return ResponseEntity.ok(ApiResponse.onSuccess(imageUrls));
    }
}