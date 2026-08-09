package com.example.bookmark.domain.collectBook.service;

import com.example.bookmark.domain.collectBook.dto.request.CollectBookCreateRequest;
import com.example.bookmark.domain.collectBook.dto.response.CollectBookCreateResponse;
import com.example.bookmark.domain.collectBook.dto.response.CollectBookDetailResponse;
import com.example.bookmark.domain.collectBook.dto.response.CollectBookListResponse;
import com.example.bookmark.domain.collectBook.entity.Chapter;
import com.example.bookmark.domain.collectBook.entity.CollectBook;
import com.example.bookmark.domain.collectBook.entity.enums.ChapterType;
import com.example.bookmark.domain.collectBook.repository.CollectBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CollectBookService {

    private final CollectBookRepository collectBookRepository;

    // 콜렉트 북 생성
    @Transactional
    public CollectBookCreateResponse createCollectBook(Long userId, CollectBookCreateRequest request) {

        List<CollectBookCreateRequest.ChapterCreateRequest> chapterRequests = request.chapters();

        // 1. 챕터 개수 및 세부 검증
        validateChapterCount(request.chapterType(), chapterRequests);

        // 2. CollectBook 엔티티 생성
        CollectBook collectBook = CollectBook.builder()
                .userId(userId)
                .title(request.title())
                .bookColor(request.coverColor())
                .year(request.year())
                .visibility(request.visibility())
                .chapterType(request.chapterType())
                .chapterNum(chapterRequests.size())
                .build();

        // 3. 전달받은 챕터 이름으로 생성 (순서 1부터 자동 할당)
        for (int i = 0; i < chapterRequests.size(); i++) {
            Chapter chapter = Chapter.builder()
                    .sequence(i + 1)
                    .name(chapterRequests.get(i).name())
                    .build();
            collectBook.addChapter(chapter);
        }

        // 4. DB 저장
        CollectBook savedBook = collectBookRepository.save(collectBook);

        // 5. 생성된 책 정보 DTO 변환 후 반환
        return CollectBookCreateResponse.from(savedBook);
    }

    private void validateChapterCount(ChapterType chapterType, List<CollectBookCreateRequest.ChapterCreateRequest> chapters) {
        if (chapters == null || chapters.isEmpty()) {
            throw new IllegalArgumentException("최소 1개 이상의 챕터가 필요합니다.");
        }

        if (chapterType == ChapterType.MONTHLY && chapters.size() != 12) {
            throw new IllegalArgumentException("월 단위 설정 시 챕터는 정확히 12개여야 합니다.");
        }

        if (chapterType == ChapterType.CUSTOM && chapters.size() > 20) {
            throw new IllegalArgumentException("직접 설정 시 챕터는 최대 20개까지 생성 가능합니다.");
        }
    }

    // 콜렉트 북 목록 조회
    @Transactional(readOnly = true)
    public List<CollectBookListResponse> getCollectBooks(Long userId) {
        return collectBookRepository.findAllByUserIdOrderByYearDesc(userId).stream()
                .map(CollectBookListResponse::from)
                .toList();
    }

    // 콜렉트 북 상세 조회
    @Transactional(readOnly = true)
    public CollectBookDetailResponse getCollectBookDetail(Long userId, Long collectBookId) {
        CollectBook collectBook = collectBookRepository.findById(collectBookId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 콜렉트북입니다. id=" + collectBookId));

        if (!collectBook.getUserId().equals(userId)) {
            throw new IllegalStateException("해당 콜렉트북을 조회할 권한이 없습니다.");
        }

        return CollectBookDetailResponse.from(collectBook);
    }

    // 콜렉트 북 삭제
    @Transactional
    public void deleteCollectBook(Long userId, Long collectBookId) {
        CollectBook collectBook = collectBookRepository.findById(collectBookId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 콜렉트북입니다. id=" + collectBookId));

        // 본인 소유의 콜렉트북인지 검증
        if (!collectBook.getUserId().equals(userId)) {
            throw new IllegalStateException("해당 콜렉트북을 삭제할 권한이 없습니다.");
        }

        collectBookRepository.delete(collectBook);
    }
}