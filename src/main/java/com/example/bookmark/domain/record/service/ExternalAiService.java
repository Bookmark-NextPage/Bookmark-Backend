package com.example.bookmark.domain.record.service;

import com.example.bookmark.common.exception.CustomException;
import com.example.bookmark.domain.record.exception.RecordErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.net.URI;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalAiService {

    @Value("${spring.ai.google.api-key}")
    private String apiKey;

    @Value("${spring.ai.google.model:gemini-3.1-flash-image-preview}")
    private String modelName;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateScrapbookImage(String prompt, List<String> imageUrls) {
        // Gemini generateContent 엔드포인트
        String url = String.format("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s", modelName, apiKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<Map<String, Object>> parts = new ArrayList<>();

        // 1. 텍스트 프롬프트 추가
        parts.add(Map.of("text", prompt));

        // 2. 참조 이미지들 다운로드 & Base64 인코딩 후 inlineData 추가
        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (String imageUrl : imageUrls) {
                try {
                    String base64Data = fetchAndEncodeImage(imageUrl);
                    if (base64Data != null) {
                        parts.add(Map.of(
                                "inlineData", Map.of(
                                        "mimeType", "image/jpeg",
                                        "data", base64Data
                                )
                        ));
                    }
                } catch (Exception e) {
                    log.warn("이미지 다운로드 및 Base64 변환 실패 (URL: {}): {}", imageUrl, e.getMessage());
                    // 개별 다운로드 실패 시 전체 요청을 중단하지 않고 건너뜀
                }
            }
        }

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of("parts", parts))
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // Gemini 응답에서 생성된 이미지 추출 (Gemini 멀티모달 이미지 생성 응답 구조 처리)
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                    List<Map<String, Object>> resParts = (List<Map<String, Object>>) content.get("parts");

                    for (Map<String, Object> part : resParts) {
                        if (part.containsKey("inlineData")) {
                            Map<String, String> inlineData = (Map<String, String>) part.get("inlineData");
                            String mimeType = inlineData.get("mimeType");
                            String data = inlineData.get("data");
                            return "data:" + mimeType + ";base64," + data;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Gemini AI API 호출 실패: {}", e.getMessage(), e);
            throw new CustomException(RecordErrorCode.AI_IMAGE_GENERATE_FAILED);
        }

        throw new CustomException(RecordErrorCode.AI_IMAGE_GENERATE_FAILED);
    }

    private String fetchAndEncodeImage(String imageUrl) {
        try (InputStream inputStream = new URI(imageUrl).toURL().openStream()) {
            byte[] bytes = inputStream.readAllBytes();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            log.error("URL 이미지 인코딩 실패: {}", imageUrl, e);
            return null;
        }
    }
}