package com.example.bookmark.global.service;

import com.example.bookmark.common.exception.CustomException;
import com.example.bookmark.domain.record.exception.RecordErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3ImageUploadService {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "webp");

    public List<String> uploadImages(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }

        if (files.size() > 5) {
            throw new CustomException(RecordErrorCode.IMAGE_COUNT_EXCEEDED, "이미지는 최대 5장까지 업로드할 수 있습니다.");
        }

        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            validateExtension(originalFilename);
            String storeFileName = createStoreFileName(originalFilename);

            try {
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(storeFileName)
                        .contentType(file.getContentType())
                        .build();

                s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

                // S3 이미지 접근 URL 생성
                String imageUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, storeFileName);
                imageUrls.add(imageUrl);

            } catch (IOException e) {
                throw new CustomException(RecordErrorCode.AI_IMAGE_GENERATE_FAILED); // 파일 업로드 예외 처리
            }
        }

        return imageUrls;
    }

    // 파일명 중복 방지를 위한 UUID 파일명 생성
    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    private void validateExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new CustomException(RecordErrorCode.INVALID_IMAGE_EXTENSION);
        }

        String ext = extractExt(originalFilename).toLowerCase(); // 대소문자 구분 없이 처리
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new CustomException(RecordErrorCode.INVALID_IMAGE_EXTENSION);
        }
    }
}