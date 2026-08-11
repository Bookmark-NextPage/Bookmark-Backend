package com.example.bookmark.domain.collectBook.service;

import com.example.bookmark.common.exception.CustomException;
import com.example.bookmark.domain.collectBook.dto.request.CollectBookCreateRequest;
import com.example.bookmark.domain.collectBook.dto.request.CollectBookVisibilityUpdateRequest;
import com.example.bookmark.domain.collectBook.dto.response.CollectBookCreateResponse;
import com.example.bookmark.domain.collectBook.dto.response.CollectBookDetailResponse;
import com.example.bookmark.domain.collectBook.dto.response.CollectBookListResponse;
import com.example.bookmark.domain.collectBook.entity.Chapter;
import com.example.bookmark.domain.collectBook.entity.CollectBook;
import com.example.bookmark.domain.collectBook.entity.enums.ChapterType;
import com.example.bookmark.domain.collectBook.entity.enums.CollectBookType;
import com.example.bookmark.domain.collectBook.exception.CollectBookErrorCode;
import com.example.bookmark.domain.collectBook.repository.CollectBookRepository;
import com.example.bookmark.domain.record.entity.Record;
import com.example.bookmark.domain.record.exception.RecordErrorCode;
import com.example.bookmark.domain.record.repository.RecordRepository;
import com.example.bookmark.domain.user.entity.User;
import com.example.bookmark.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CollectBookService {

    private final CollectBookRepository collectBookRepository;
    private final UserRepository userRepository;
    private final RecordRepository recordRepository; // 👈 추가된 의존성

    @Transactional
    public CollectBookCreateResponse createCollectBook(Long userId, CollectBookCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(RecordErrorCode.USER_NOT_FOUND));

        List<CollectBookCreateRequest.ChapterCreateRequest> chapterRequests = request.chapters();
        validateChapterCount(request.chapterType(), chapterRequests);

        CollectBook collectBook = CollectBook.builder()
                .user(user)
                .title(request.title())
                .bookColor(request.coverColor())
                .year(request.year())
                .visibility(request.visibility())
                .chapterType(request.chapterType())
                .chapterNum(chapterRequests.size())
                .collectBookType(CollectBookType.CUSTOM)
                .build();

        for (int i = 0; i < chapterRequests.size(); i++) {
            Chapter chapter = Chapter.builder()
                    .sequence(i + 1)
                    .name(chapterRequests.get(i).name())
                    .build();
            collectBook.addChapter(chapter);
        }

        CollectBook savedBook = collectBookRepository.save(collectBook);
        return CollectBookCreateResponse.from(savedBook);
    }

    private void validateChapterCount(ChapterType chapterType, List<CollectBookCreateRequest.ChapterCreateRequest> chapters) {
        if (chapters == null || chapters.isEmpty()) {
            throw new CustomException(CollectBookErrorCode.CHAPTER_COUNT_INVALID, "최소 1개 이상의 챕터가 필요합니다.");
        }

        if (chapterType == ChapterType.MONTHLY && chapters.size() != 12) {
            throw new CustomException(CollectBookErrorCode.CHAPTER_COUNT_INVALID, "월 단위 설정 시 챕터는 정확히 12개여야 합니다.");
        }

        if (chapterType == ChapterType.CUSTOM && chapters.size() > 20) {
            throw new CustomException(CollectBookErrorCode.CHAPTER_COUNT_INVALID, "직접 설정 시 챕터는 최대 20개까지 생성 가능합니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<CollectBookListResponse> getCollectBooks(Long userId) {
        return collectBookRepository.findAllByUserIdOrderByYearDesc(userId).stream()
                .map(CollectBookListResponse::from)
                .toList();
    }

    // 💡 수정된 상세 조회 메서드
    @Transactional(readOnly = true)
    public CollectBookDetailResponse getCollectBookDetail(Long userId, Long collectBookId) {
        CollectBook collectBook = collectBookRepository.findById(collectBookId)
                .orElseThrow(() -> new CustomException(CollectBookErrorCode.COLLECT_BOOK_NOT_FOUND));

        if (!collectBook.getUser().getId().equals(userId)) {
            throw new CustomException(CollectBookErrorCode.COLLECT_BOOK_FORBIDDEN);
        }

        // 1. 해당 콜렉트북의 챕터 ID 목록 추출
        List<Long> chapterIds = collectBook.getChapters().stream()
                .map(Chapter::getId)
                .toList();

        // 2. 챕터 ID 목록으로 포함된 모든 기록 조회 후 챕터 ID 기준 Map 분할
        List<Record> allRecords = recordRepository.findAllByChapterIdInOrderByIdDesc(chapterIds);
        Map<Long, List<Record>> recordMapByChapter = allRecords.stream()
                .collect(Collectors.groupingBy(record -> record.getChapter().getId()));

        // 3. DTO 변환 및 반환
        return CollectBookDetailResponse.of(collectBook, recordMapByChapter);
    }

    @Transactional
    public void deleteCollectBook(Long userId, Long collectBookId) {
        CollectBook collectBook = collectBookRepository.findById(collectBookId)
                .orElseThrow(() -> new CustomException(CollectBookErrorCode.COLLECT_BOOK_NOT_FOUND));

        if (!collectBook.getUser().getId().equals(userId)) {
            throw new CustomException(CollectBookErrorCode.COLLECT_BOOK_FORBIDDEN);
        }

        if (collectBook.isSystemType()) {
            throw new CustomException(CollectBookErrorCode.SYSTEM_COLLECT_BOOK_DELETE_NOT_ALLOWED);
        }

        collectBookRepository.delete(collectBook);
    }

    @Transactional
    public void updateVisibility(Long userId, Long collectBookId, CollectBookVisibilityUpdateRequest request) {
        CollectBook collectBook = collectBookRepository.findById(collectBookId)
                .orElseThrow(() -> new CustomException(CollectBookErrorCode.COLLECT_BOOK_NOT_FOUND));

        if (!collectBook.getUser().getId().equals(userId)) {
            throw new CustomException(CollectBookErrorCode.COLLECT_BOOK_FORBIDDEN);
        }

        collectBook.updateVisibility(request.visibility());
    }
}