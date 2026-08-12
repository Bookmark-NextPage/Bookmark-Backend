package com.example.bookmark.global.infrastructure.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

@Service
@Primary // StorageService 주입 시 이 구현체를 우선 사용
@RequiredArgsConstructor
public class S3StorageService implements StorageService {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Override
    public String uploadFromUrl(String sourceImageUrl) {

        try {

            String extension = "png";
            byte[] imageBytes;

            // ===== AI 생성 Base64 이미지 =====
            if (sourceImageUrl.startsWith("data:image")) {

                String meta =
                        sourceImageUrl.substring(
                                0,
                                sourceImageUrl.indexOf(",")
                        );

                if (meta.contains("image/jpeg")) {
                    extension = "jpg";
                } else if (meta.contains("image/png")) {
                    extension = "png";
                }

                String base64Data =
                        sourceImageUrl.substring(
                                sourceImageUrl.indexOf(",") + 1
                        );

                imageBytes = Base64.getDecoder().decode(base64Data);

            } else {

                // 일반 URL 이미지
                URL url = new URL(sourceImageUrl);

                try (InputStream in = url.openStream()) {
                    imageBytes = in.readAllBytes();
                }
            }

            String fileName =
                    UUID.randomUUID() + "." + extension;

            PutObjectRequest putObjectRequest =
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(fileName)
                            .contentType("image/" + extension)
                            .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(
                            new ByteArrayInputStream(imageBytes),
                            imageBytes.length
                    )
            );

            return String.format(
                    "https://%s.s3.%s.amazonaws.com/%s",
                    bucket,
                    region,
                    fileName
            );

        } catch (IOException e) {

            throw new RuntimeException("S3 이미지 업로드 실패", e);
        }
    }
}