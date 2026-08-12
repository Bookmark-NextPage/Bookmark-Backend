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

    @Transactional
    public AiImageGenerateResponse generateScrapImage(
            Long userId,
            AiImageGenerateRequest request
    ) {

        // 1. 하루 생성 횟수 제한
        validateDailyLimit(userId);

        // 2. 프롬프트 생성
        String prompt = buildPrompt(request);

        // 3. Vertex AI 이미지 생성
        String rawAiImage =
                externalAiService.generateScrapbookImage(
                        prompt,
                        request.getImageUrls()
                );

        // 4. S3 업로드
        String storedImageUrl =
                storageService.uploadFromUrl(rawAiImage);

        // 5. 생성 로그 저장
        recordAiLogRepository.save(
                RecordAiLog.builder()
                        .userId(userId)
                        .build()
        );

        // 6. 응답 반환
        return new AiImageGenerateResponse(storedImageUrl);
    }

    private void validateDailyLimit(Long userId) {

        LocalDateTime startOfDay =
                LocalDate.now().atStartOfDay();

        LocalDateTime endOfDay =
                LocalDate.now().atTime(LocalTime.MAX);

        long count =
                recordAiLogRepository.countByUserIdAndCreatedAtBetween(
                        userId,
                        startOfDay,
                        endOfDay
                );

        if (count >= DAILY_MAX_LIMIT) {

            throw new CustomException(
                    RecordErrorCode.AI_RATE_LIMIT_EXCEEDED
            );
        }
    }

    /**
     * title + content 는 항상 반영
     * imageUrls, keywordIds 는 값이 있을 때만 반영
     * feedback 이 있으면 최우선으로 반영
     */
    private String buildPrompt(AiImageGenerateRequest request) {

        StringBuilder sb = new StringBuilder();

        sb.append("You are a professional scrapbook illustrator.\n");
        sb.append("Generate ONE scrapbook-style image.\n");
        sb.append("The image must visually represent the user's diary.\n\n");

        // ===== 필수값 =====
        sb.append("[PRIMARY DIARY INFORMATION]\n");
        sb.append("Title: ")
                .append(request.getTitle())
                .append("\n");

        sb.append("Diary: ")
                .append(limitLength(request.getContent(), 2000))
                .append("\n\n");

        // ===== 선택값: 키워드 =====
        if (request.getKeywordIds() != null
                && !request.getKeywordIds().isEmpty()) {

            List<Keyword> keywords =
                    keywordRepository.findAllById(
                            request.getKeywordIds()
                    );

            String keywordNames = keywords.stream()
                    .map(Keyword::getName)
                    .collect(Collectors.joining(", "));

            if (!keywordNames.isBlank()) {

                sb.append("[MOOD KEYWORDS]\n");
                sb.append(keywordNames).append("\n\n");
            }
        }

        // ===== 선택값: 첨부 이미지 =====
        if (request.getImageUrls() != null
                && !request.getImageUrls().isEmpty()) {

            sb.append("[ATTACHED PHOTOS]\n");
            sb.append("Use the attached photos as visual references.\n");
            sb.append("Preserve the main subjects, atmosphere, lighting, and color mood.\n");
            sb.append("Integrate them naturally as photo cutouts in the scrapbook.\n\n");
        }

        // ===== 재생성 피드백 =====
        if (request.getFeedback() != null
                && !request.getFeedback().isBlank()) {

            sb.append("[REVISION REQUEST - HIGHEST PRIORITY]\n");
            sb.append(request.getFeedback()).append("\n\n");
        }

        // ===== 스타일 지침 =====
        sb.append("[STYLE REQUIREMENTS]\n");
        sb.append("- Warm emotional scrapbook aesthetic\n");
        sb.append("- Torn paper textures\n");
        sb.append("- Washi tape and stickers\n");
        sb.append("- Handwritten doodles and memo notes\n");
        sb.append("- Layered collage composition\n");
        sb.append("- Soft natural colors unless feedback requests otherwise\n");
        sb.append("- Keep the overall mood consistent with the diary text\n\n");

        sb.append("Return one final scrapbook illustration only.");

        return sb.toString();
    }

    private String limitLength(String text, int maxLength) {

        if (text == null) {
            return "";
        }

        return text.length() > maxLength
                ? text.substring(0, maxLength)
                : text;
    }
}