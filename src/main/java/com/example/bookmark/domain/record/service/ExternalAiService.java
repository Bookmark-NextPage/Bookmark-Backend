package com.example.bookmark.domain.record.service;

import com.example.bookmark.common.exception.CustomException;
import com.example.bookmark.domain.record.exception.RecordErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalAiService {

    @Value("${spring.ai.google.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateScrapbookImage(String prompt) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/imagen-3.0-generate-002:predict?key=" + apiKey;        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> promptMap = Map.of("prompt", prompt);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("sampleCount", 1);
        parameters.put("aspectRatio", "1:1");
        parameters.put("outputMimeType", "image/png");

        requestBody.put("instances", List.of(promptMap));
        requestBody.put("parameters", parameters);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<Map<String, Object>> predictions = (List<Map<String, Object>>) response.getBody().get("predictions");
                if (predictions != null && !predictions.isEmpty()) {
                    String bytesBase64Encoded = (String) predictions.get(0).get("bytesBase64Encoded");
                    return "data:image/png;base64," + bytesBase64Encoded;
                }
            }
        } catch (Exception e) {
            log.error("Gemini Imagen API 호출 실패: {}", e.getMessage(), e);
            throw new CustomException(RecordErrorCode.AI_IMAGE_GENERATE_FAILED);
        }

        throw new CustomException(RecordErrorCode.AI_IMAGE_GENERATE_FAILED);
    }
}