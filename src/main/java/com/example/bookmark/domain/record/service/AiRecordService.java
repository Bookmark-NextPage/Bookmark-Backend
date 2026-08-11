package com.example.bookmark.domain.record.service;

import com.example.bookmark.common.exception.CustomException;
import com.example.bookmark.domain.record.dto.request.AiImageGenerateRequest;
import com.example.bookmark.domain.record.dto.response.AiImageGenerateResponse;
import com.example.bookmark.domain.record.entity.Keyword;
import com.example.bookmark.domain.record.entity.RecordAiLog;
import com.example.bookmark.domain.record.exception.RecordErrorCode;
import com.example.bookmark.domain.record.repository.KeywordRepository;
import com.example.bookmark.domain.record.repository.RecordAiLogRepository;
import com.example.bookmark.global.infrastructure.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AiRecordService {

    private final ExternalAiService externalAiService;
    private final StorageService storageService;
    private final KeywordRepository keywordRepository;
    private final RecordAiLogRepository recordAiLogRepository;

    private static final int DAILY_MAX_LIMIT = 10;

    /**
     * AI 감성 스크랩북 이미지 생성/재생성 API
     */
    @Transactional
    public AiImageGenerateResponse generateScrapImage(Long userId, AiImageGenerateRequest request) {

        // 1. [검증] 오늘 하루 생성 횟수 체크 (최대 10회)
        validateDailyLimit(userId);

        // 2. 프롬프트 생성
        String prompt = buildPrompt(userId, request);

        // 3. Gemini API 호출
        String rawAiImage = externalAiService.generateScrapbookImage(prompt);

        // 4. 로컬/S3 스토리지 업로드
        String storedImageUrl = storageService.uploadFromUrl(rawAiImage);

        // 5. [기록] AI 생성 성공 시 오늘 사용 횟수 카운트 로그 저장
        recordAiLogRepository.save(RecordAiLog.builder()
                .userId(userId)
                .build());

        // 6. 결과 반환
        return new AiImageGenerateResponse(storedImageUrl);
    }

    private void validateDailyLimit(Long userId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        long count = recordAiLogRepository.countByUserIdAndCreatedAtBetween(userId, startOfDay, endOfDay);

        if (count >= DAILY_MAX_LIMIT) {
            throw new CustomException(RecordErrorCode.AI_RATE_LIMIT_EXCEEDED);
        }
    }

    private String buildPrompt(Long userId, AiImageGenerateRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("Title: ").append(request.getTitle()).append("\n");
        sb.append("Content: ").append(request.getContent()).append("\n");

        if (request.getKeywordIds() != null && !request.getKeywordIds().isEmpty()) {
            List<Keyword> keywords = keywordRepository.findAllById(request.getKeywordIds());
            String keywordNames = keywords.stream()
                    .map(Keyword::getName)
                    .collect(Collectors.joining(", "));
            if (!keywordNames.isBlank()) {
                sb.append("Keywords/Moods: ").append(keywordNames).append("\n");
            }
        }

        if (request.getFeedback() != null && !request.getFeedback().isBlank()) {
            sb.append("User Feedback for Revision: ").append(request.getFeedback()).append("\n");
        }

        sb.append("Please generate an artistic scrapbook style image based on this description.");
        return sb.toString();
    }
}