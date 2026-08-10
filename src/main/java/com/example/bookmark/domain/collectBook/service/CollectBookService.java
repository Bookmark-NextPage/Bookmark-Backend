package com.example.bookmark.domain.collectBook.service;

import com.example.bookmark.domain.collectBook.dto.request.CollectBookCreateRequest;
import com.example.bookmark.domain.collectBook.dto.request.CollectBookVisibilityUpdateRequest;
import com.example.bookmark.domain.collectBook.dto.response.CollectBookCreateResponse;
import com.example.bookmark.domain.collectBook.dto.response.CollectBookDetailResponse;
import com.example.bookmark.domain.collectBook.dto.response.CollectBookListResponse;
import com.example.bookmark.domain.collectBook.entity.Chapter;
import com.example.bookmark.domain.collectBook.entity.CollectBook;
import com.example.bookmark.domain.collectBook.entity.enums.ChapterType;
import com.example.bookmark.domain.collectBook.entity.enums.CollectBookType;
import com.example.bookmark.domain.collectBook.repository.CollectBookRepository;
import com.example.bookmark.domain.user.entity.User;
import com.example.bookmark.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CollectBookService {

    private final CollectBookRepository collectBookRepository;
    private final UserRepository userRepository; // ✨ UserRepository 추가

    @Transactional
    public CollectBookCreateResponse createCollectBook(Long userId, CollectBookCreateRequest request) {
        // ✨ User FK 조회를 통해 유저 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. id=" + userId));

        List<CollectBookCreateRequest.ChapterCreateRequest> chapterRequests = request.chapters();
        validateChapterCount(request.chapterType(), chapterRequests);

        CollectBook collectBook = CollectBook.builder()
                .user(user) // ✨ User 엔티티 주입
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
            throw new IllegalArgumentException("최소 1개 이상의 챕터가 필요합니다.");
        }

        if (chapterType == ChapterType.MONTHLY && chapters.size() != 12) {
            throw new IllegalArgumentException("월 단위 설정 시 챕터는 정확히 12개여야 합니다.");
        }

        if (chapterType == ChapterType.CUSTOM && chapters.size() > 20) {
            throw new IllegalArgumentException("직접 설정 시 챕터는 최대 20개까지 생성 가능합니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<CollectBookListResponse> getCollectBooks(Long userId) {
        return collectBookRepository.findAllByUserIdOrderByYearDesc(userId).stream()
                .map(CollectBookListResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CollectBookDetailResponse getCollectBookDetail(Long userId, Long collectBookId) {
        CollectBook collectBook = collectBookRepository.findById(collectBookId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 콜렉트북입니다. id=" + collectBookId));

        if (!collectBook.getUser().getId().equals(userId)) {
            throw new IllegalStateException("해당 콜렉트북을 조회할 권한이 없습니다.");
        }

        return CollectBookDetailResponse.from(collectBook);
    }

    @Transactional
    public void deleteCollectBook(Long userId, Long collectBookId) {
        CollectBook collectBook = collectBookRepository.findById(collectBookId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 콜렉트북입니다. id=" + collectBookId));

        if (!collectBook.getUser().getId().equals(userId)) {
            throw new IllegalStateException("해당 콜렉트북을 삭제할 권한이 없습니다.");
        }

        if (collectBook.isSystemType()) {
            throw new IllegalArgumentException("시스템에서 자동 생성된 콜렉트북은 삭제할 수 없습니다.");
        }

        collectBookRepository.delete(collectBook);
    }

    @Transactional
    public void updateVisibility(Long userId, Long collectBookId, CollectBookVisibilityUpdateRequest request) {
        CollectBook collectBook = collectBookRepository.findById(collectBookId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 콜렉트북입니다. id=" + collectBookId));

        if (!collectBook.getUser().getId().equals(userId)) {
            throw new IllegalStateException("해당 콜렉트북의 공개 범위를 수정할 권한이 없습니다.");
        }

        collectBook.updateVisibility(request.visibility());
    }
}