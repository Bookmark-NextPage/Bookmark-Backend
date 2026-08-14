package com.example.bookmark.domain.record.service;

import com.example.bookmark.common.exception.CustomException;
import com.example.bookmark.domain.record.exception.RecordErrorCode;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Blob;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.Part;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
public class ExternalAiService {

    @Value("${spring.cloud.gcp.project-id}")
    private String projectId;

    private static final String LOCATION = "us-central1";

    // Gemini 이미지 생성 모델
    private static final String MODEL_NAME = "gemini-2.5-flash-image";

    public String generateScrapbookImage(String prompt, List<String> imageUrls) {

        try (VertexAI vertexAI = new VertexAI(projectId, LOCATION)) {

            GenerativeModel model =
                    new GenerativeModel(MODEL_NAME, vertexAI);

            List<Part> parts = new ArrayList<>();

            // ===== 텍스트 프롬프트 =====
            Part textPart = Part.newBuilder()
                    .setText(prompt)
                    .build();

            parts.add(textPart);

            // ===== 첨부 이미지들 (선택값) =====
            if (imageUrls != null && !imageUrls.isEmpty()) {

                for (String imageUrl : imageUrls) {

                    try {

                        byte[] imageBytes = downloadImage(imageUrl);

                        String mimeType = detectMimeType(imageUrl);

                        Part imagePart = Part.newBuilder()
                                .setInlineData(
                                        Blob.newBuilder()
                                                .setMimeType(mimeType)
                                                .setData(ByteString.copyFrom(imageBytes))
                                                .build()
                                )
                                .build();

                        parts.add(imagePart);

                    } catch (IllegalArgumentException e) {

                        log.warn("지원하지 않는 이미지 형식: {}", imageUrl);

                    } catch (Exception e) {

                        log.warn("첨부 이미지 다운로드 실패: {}", imageUrl, e);
                    }
                }
            }

            // ===== 요청 Content 생성 =====
            Content content = Content.newBuilder()
                    .setRole("user")
                    .addAllParts(parts)
                    .build();

            // ===== Gemini 호출 =====
            GenerateContentResponse response = model.generateContent(content);

            // ===== 생성된 이미지 추출 =====
            return extractImage(response);

        } catch (Exception e) {

            log.error("Vertex AI 이미지 생성 실패", e);

            throw new CustomException(
                    RecordErrorCode.AI_IMAGE_GENERATE_FAILED
            );
        }
    }

    private String extractImage(GenerateContentResponse response) {

        var candidates = response.getCandidatesList();

        if (candidates == null || candidates.isEmpty()) {

            throw new CustomException(
                    RecordErrorCode.AI_IMAGE_GENERATE_FAILED
            );
        }

        var responseParts =
                candidates.get(0)
                        .getContent()
                        .getPartsList();

        for (var part : responseParts) {

            if (part.hasInlineData()) {

                String mimeType =
                        part.getInlineData().getMimeType();

                String base64 =
                        Base64.getEncoder()
                                .encodeToString(
                                        part.getInlineData()
                                                .getData()
                                                .toByteArray()
                                );

                return "data:" + mimeType + ";base64," + base64;
            }
        }

        throw new CustomException(
                RecordErrorCode.AI_IMAGE_GENERATE_FAILED
        );
    }

    private byte[] downloadImage(String imageUrl) throws Exception {

        try (InputStream inputStream =
                     new URI(imageUrl).toURL().openStream()) {

            return inputStream.readAllBytes();
        }
    }

    /**
     * jpg, jpeg, png 만 허용
     */
    private String detectMimeType(String imageUrl) {

        String lower = imageUrl.toLowerCase();

        if (lower.endsWith(".png")) {
            return "image/png";
        }

        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "image/jpeg";
        }

        throw new IllegalArgumentException(
                "지원하지 않는 이미지 형식입니다. jpg, jpeg, png만 가능합니다."
        );
    }
}