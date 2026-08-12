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
    public AiImageGenerateResponse generateScrapImage(Long userId, AiImageGenerateRequest request) {

        // 1. [검증] 오늘 하루 생성 횟수 체크 (최대 10회)
        validateDailyLimit(userId);

        // 2. 구조화된 프롬프트 생성
        String prompt = buildPrompt(userId, request);

        // 3. Gemini 멀티모달 API 호출 (수정: prompt와 실제 이미지 URL 목록을 함께 전달)
        String rawAiImage = externalAiService.generateScrapbookImage(prompt, request.getImageUrls());

        // 4. 로컬/S3 스토리지 업로드
        String storedImageUrl = storageService.uploadFromUrl(rawAiImage);

        // 5. [기록] AI 생성 성공 로그 저장
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

        sb.append("Role: Professional digital scrapbook designer.\n");
        sb.append("Task: Create an artistic scrapbook style collage image based on the following journal entry.\n\n");

        // [필수] 제목 및 본문
        sb.append("### Journal Entry\n");
        sb.append("- Title: ").append(request.getTitle()).append("\n");
        sb.append("- Content: ").append(request.getContent()).append("\n\n");

        // [선택] 감성 키워드
        if (request.getKeywordIds() != null && !request.getKeywordIds().isEmpty()) {
            List<Keyword> keywords = keywordRepository.findAllById(request.getKeywordIds());
            String keywordNames = keywords.stream()
                    .map(Keyword::getName)
                    .collect(Collectors.joining(", "));
            if (!keywordNames.isBlank()) {
                sb.append("### Mood & Keywords\n");
                sb.append("- Mood: ").append(keywordNames).append("\n\n");
            }
        }

        // [선택] 첨부 이미지 안내 (수정: 실제 전달된 이미지를 레이아웃에 자연스럽게 통합하도록 지침 변경)
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            int imageCount = request.getImageUrls().size();
            sb.append("### Photo Layout Guidance\n");
            sb.append("- ").append(imageCount)
                    .append(" image(s) are attached in this request. Seamlessly integrate the visual elements, mood, and key subjects of these attached photos into the scrapbook layout as cohesive photo cutouts.\n\n");
        }

        // [재생성 시] 사용자 피드백 최우선 순위 지정
        if (request.getFeedback() != null && !request.getFeedback().isBlank()) {
            sb.append("### CRITICAL REVISION INSTRUCTION (Highest Priority)\n");
            sb.append("The user requested the following revision: \"")
                    .append(request.getFeedback())
                    .append("\". Strictly prioritize this feedback above all other design elements while recreating the image.\n\n");
        }

        // 디자인 스타일 지침
        sb.append("### Design Style\n");
        sb.append("- Create a cohesive scrapbook layout featuring torn paper textures, tape, stickers, decorative borders, and handwritten vibe elements.");

        return sb.toString();
    }
}