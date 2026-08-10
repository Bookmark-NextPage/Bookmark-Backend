package com.example.bookmark.domain.record.service;

import com.example.bookmark.domain.bucketBoard.entity.BucketBoardMemo;
import com.example.bookmark.domain.bucketBoard.repository.BucketBoardMemoRepository;
import com.example.bookmark.domain.collectBook.entity.Chapter;
import com.example.bookmark.domain.collectBook.entity.CollectBook;
import com.example.bookmark.domain.collectBook.entity.enums.CollectBookType;
import com.example.bookmark.domain.collectBook.entity.enums.Visibility;
import com.example.bookmark.domain.collectBook.repository.ChapterRepository;
import com.example.bookmark.domain.collectBook.repository.CollectBookRepository;
import com.example.bookmark.domain.friend.entity.enums.FriendStatus;
import com.example.bookmark.domain.friend.repository.FriendRepository;
import com.example.bookmark.domain.record.dto.request.CommentCreateRequest;
import com.example.bookmark.domain.record.dto.request.RecordCreateRequest;
import com.example.bookmark.domain.record.dto.request.RecordDraftSaveRequest;
import com.example.bookmark.domain.record.dto.response.RecordDetailResponse;
import com.example.bookmark.domain.record.dto.response.RecordSaveResponse;
import com.example.bookmark.domain.record.entity.*;
import com.example.bookmark.domain.record.entity.Record;
import com.example.bookmark.domain.record.entity.enums.RecordStatus;
import com.example.bookmark.domain.record.repository.KeywordRepository;
import com.example.bookmark.domain.record.repository.RecordCommentRepository;
import com.example.bookmark.domain.record.repository.RecordLikeRepository;
import com.example.bookmark.domain.record.repository.RecordRepository;
import com.example.bookmark.domain.user.entity.User;
import com.example.bookmark.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecordService {

    private final RecordRepository recordRepository;
    private final ChapterRepository chapterRepository;
    private final CollectBookRepository collectBookRepository;
    private final KeywordRepository keywordRepository;
    private final UserRepository userRepository;
    private final BucketBoardMemoRepository bucketBoardMemoRepository;
    private final FriendRepository friendRepository;
    private final RecordCommentRepository recordCommentRepository;
    private final RecordLikeRepository recordLikeRepository;

    // 1. 콜렉트북 기록 상세 조회 (공개 범위 Visibility 검증 추가)
    public RecordDetailResponse getRecordDetail(Long userId, Long collectBookId, Long recordId) {
        CollectBook collectBook = collectBookRepository.findById(collectBookId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 콜렉트북입니다. id=" + collectBookId));

        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기록입니다. id=" + recordId));

        if (record.getChapter() == null || !record.getChapter().getCollectBook().getId().equals(collectBookId)) {
            throw new IllegalArgumentException("해당 콜렉트북에 속한 기록이 아닙니다.");
        }

        if (record.getStatus() == RecordStatus.DRAFT && !record.getUser().getId().equals(userId)) {
            throw new IllegalStateException("임시 저장된 기록은 작성자 본인만 조회할 수 있습니다.");
        }

        // 콜렉트북 공개 범위(Visibility) 검증
        validateCollectBookVisibility(userId, collectBook);

        // 좋아요 수, 로그인한 유저의 좋아요 클릭 여부, 댓글 리스트 연동
        Long likeCount = record.getLikes() != null ? record.getLikes() : 0L;
        Boolean isLiked = recordLikeRepository.existsByRecordIdAndUserId(recordId, userId);

        List<RecordComment> recordComments = recordCommentRepository.findAllByRecordIdOrderByCreatedAtAsc(recordId);
        List<RecordDetailResponse.CommentResponse> comments = recordComments.stream()
                .map(c -> RecordDetailResponse.CommentResponse.builder()
                        .commentId(c.getId())
                        .userId(c.getUser().getId())
                        .nickname(c.getUser().getName())
                        .content(c.getContent())
                        .createdAt(c.getCreatedAt())
                        .build())
                .toList();

        return RecordDetailResponse.of(record, likeCount, isLiked, comments);
    }

    // 2. 임시 저장
    @Transactional
    public RecordSaveResponse saveDraft(Long userId, RecordDraftSaveRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. id=" + userId));

        Record record;

        if (request.getRecordId() != null) {
            record = recordRepository.findById(request.getRecordId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 임시저장 기록입니다. id=" + request.getRecordId()));

            if (!record.getUser().getId().equals(userId)) {
                throw new IllegalStateException("해당 기록을 수정할 권한이 없습니다.");
            }

            record.updateRecord(record.getChapter(), request.getTitle(), request.getContent(), RecordStatus.DRAFT);
            record.clearImagesAndKeywords();
        } else {
            record = Record.builder()
                    .user(user)
                    .title(request.getTitle())
                    .content(request.getContent())
                    .status(RecordStatus.DRAFT)
                    .build();
        }

        attachImagesAndKeywords(record, request.getImageUrls(), request.getKeywordIds());
        Record savedRecord = recordRepository.save(record);
        return RecordSaveResponse.from(savedRecord);
    }

    // 3. 최근 임시 저장 조회
    public RecordSaveResponse getLatestDraft(Long userId) {
        Record draftRecord = recordRepository.findFirstByUserIdAndStatusOrderByCreatedAtDesc(userId, RecordStatus.DRAFT)
                .orElse(null);

        return draftRecord != null ? RecordSaveResponse.from(draftRecord) : null;
    }

    // 4. 콜렉트북에 기록 생성 - 메모지 기반 X
    @Transactional
    public RecordSaveResponse createCustomRecord(Long userId, Long collectBookId, Long chapterId, RecordCreateRequest request) {
        CollectBook collectBook = collectBookRepository.findById(collectBookId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 콜렉트북입니다. id=" + collectBookId));

        if (!collectBook.getUser().getId().equals(userId)) {
            throw new IllegalStateException("해당 콜렉트북에 작성할 권한이 없습니다.");
        }

        Chapter targetChapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 챕터입니다. id=" + chapterId));

        return savePublishedRecord(userId, targetChapter, null, request);
    }

    // 5. 콜렉트북에 기록 생성 - 메모지 기반 O
    @Transactional
    public RecordSaveResponse createMemoRecord(Long userId, Long memoId, RecordCreateRequest request) {
        LocalDate now = LocalDate.now();
        Chapter systemChapter = findSystemMonthlyChapter(userId, now.getYear(), now.getMonthValue());

        BucketBoardMemo memo = bucketBoardMemoRepository.findById(memoId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 버킷보드 메모입니다. id=" + memoId));

        return savePublishedRecord(userId, systemChapter, memo, request);
    }

    private RecordSaveResponse savePublishedRecord(Long userId, Chapter chapter, BucketBoardMemo memo, RecordCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. id=" + userId));

        Record record;

        if (request.getRecordId() != null) {
            record = recordRepository.findById(request.getRecordId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기록입니다. id=" + request.getRecordId()));

            if (!record.getUser().getId().equals(userId)) {
                throw new IllegalStateException("해당 기록을 수정할 권한이 없습니다.");
            }

            record.updateRecord(chapter, request.getTitle(), request.getContent(), RecordStatus.PUBLISHED);
            record.clearImagesAndKeywords();
        } else {
            record = Record.builder()
                    .user(user)
                    .chapter(chapter)
                    .bucketBoardMemo(memo)
                    .title(request.getTitle())
                    .content(request.getContent())
                    .status(RecordStatus.PUBLISHED)
                    .build();
        }

        attachImagesAndKeywords(record, request.getImageUrls(), request.getKeywordIds());
        Record savedRecord = recordRepository.save(record);
        return RecordSaveResponse.from(savedRecord);
    }

    // 6. 댓글 작성 (친구 관계 검증)
    @Transactional
    public void createComment(Long userId, Long collectBookId, Long recordId, CommentCreateRequest request) {
        Record record = validateRecordAndFriendship(userId, collectBookId, recordId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. id=" + userId));

        RecordComment comment = RecordComment.builder()
                .record(record)
                .user(user)
                .content(request.getContent())
                .build();

        recordCommentRepository.save(comment);
    }

    // 7. 좋아요 등록 (친구 관계 검증)
    @Transactional
    public void addLike(Long userId, Long collectBookId, Long recordId) {
        Record record = validateRecordAndFriendship(userId, collectBookId, recordId);

        if (recordLikeRepository.existsByRecordIdAndUserId(recordId, userId)) {
            throw new IllegalStateException("이미 좋아요를 누른 기록입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. id=" + userId));

        recordLikeRepository.save(RecordLike.builder().record(record).user(user).build());
    }

    // 8. 좋아요 취소 (삭제)
    @Transactional
    public void deleteLike(Long userId, Long collectBookId, Long recordId) {
        validateRecordAndFriendship(userId, collectBookId, recordId);

        RecordLike recordLike = recordLikeRepository.findByRecordIdAndUserId(recordId, userId)
                .orElseThrow(() -> new IllegalArgumentException("좋아요 기록을 찾을 수 없습니다."));

        recordLikeRepository.delete(recordLike);
    }

    private Chapter findSystemMonthlyChapter(Long userId, int year, int month) {
        CollectBook systemCollectBook = collectBookRepository.findByUserIdAndYearAndCollectBookType(userId, year, CollectBookType.SYSTEM)
                .orElseThrow(() -> new IllegalArgumentException(year + "년도 시스템 자동 생성 콜렉트북을 찾을 수 없습니다."));

        return systemCollectBook.getChapters().stream()
                .filter(ch -> ch.getSequence() == month)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(month + "월 챕터를 찾을 수 없습니다."));
    }

    private void attachImagesAndKeywords(Record record, List<String> imageUrls, List<Long> keywordIds) {
        if (imageUrls != null && !imageUrls.isEmpty()) {
            int seq = 1;
            for (String url : imageUrls) {
                record.addImage(RecordImage.builder().imageUrl(url).imageSeq(seq++).build());
            }
        }
        if (keywordIds != null && !keywordIds.isEmpty()) {
            List<Keyword> keywords = keywordRepository.findAllById(keywordIds);
            for (Keyword keyword : keywords) {
                record.addRecordKeyword(RecordKeyword.builder().keyword(keyword).build());
            }
        }
    }

    // 양방향 친구 관계 검증 helper 메서드
    private boolean isFriend(Long userId, Long targetUserId) {
        if (userId.equals(targetUserId)) return true; // 본인은 항상 허용
        return friendRepository.existsByUserIdAndFriendUserIdAndStatus(userId, targetUserId, FriendStatus.ACCEPTED)
                || friendRepository.existsByUserIdAndFriendUserIdAndStatus(targetUserId, userId, FriendStatus.ACCEPTED);
    }

    // 콜렉트북 공개 범위(Visibility) 검증 helper 메서드
    private void validateCollectBookVisibility(Long userId, CollectBook collectBook) {
        Long ownerId = collectBook.getUser().getId();
        Visibility visibility = collectBook.getVisibility();

        // 1. PRIVATE인 경우 본인만 접근 가능
        if (visibility == Visibility.PRIVATE && !ownerId.equals(userId)) {
            throw new IllegalStateException("비공개 콜렉트북입니다.");
        }

        // 2. FRIENDS인 경우 친구 및 본인만 접근 가능
        if (visibility == Visibility.FRIENDS && !isFriend(userId, ownerId)) {
            throw new IllegalStateException("친구에게만 공개된 콜렉트북입니다.");
        }
    }

    // 공통 검증 helper: 기록 존재 여부 및 친구 관계(본인 포함) 검증
    private Record validateRecordAndFriendship(Long userId, Long collectBookId, Long recordId) {
        CollectBook collectBook = collectBookRepository.findById(collectBookId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 콜렉트북입니다. id=" + collectBookId));

        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기록입니다. id=" + recordId));

        if (record.getChapter() == null || !record.getChapter().getCollectBook().getId().equals(collectBookId)) {
            throw new IllegalArgumentException("해당 콜렉트북에 속한 기록이 아닙니다.");
        }

        // 댓글/좋아요 전 콜렉트북 자체 접근 가능 여부 우선 검증
        validateCollectBookVisibility(userId, collectBook);

        Long ownerId = record.getUser().getId();

        if (!isFriend(userId, ownerId)) {
            throw new IllegalStateException("친구 관계인 유저만 댓글 및 좋아요를 남길 수 있습니다.");
        }

        return record;
    }
}