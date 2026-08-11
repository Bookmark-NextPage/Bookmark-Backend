package com.example.bookmark.domain.record.service;

import com.example.bookmark.common.exception.CustomException;
import com.example.bookmark.domain.bucketBoard.entity.BucketBoardMemo;
import com.example.bookmark.domain.bucketBoard.repository.BucketBoardMemoRepository;
import com.example.bookmark.domain.collectBook.entity.Chapter;
import com.example.bookmark.domain.collectBook.entity.CollectBook;
import com.example.bookmark.domain.collectBook.entity.enums.CollectBookType;
import com.example.bookmark.domain.collectBook.entity.enums.Visibility;
import com.example.bookmark.domain.collectBook.exception.CollectBookErrorCode;
import com.example.bookmark.domain.collectBook.repository.ChapterRepository;
import com.example.bookmark.domain.collectBook.repository.CollectBookRepository;
import com.example.bookmark.domain.friend.entity.enums.FriendStatus;
import com.example.bookmark.domain.friend.repository.FriendRepository;
import com.example.bookmark.domain.record.dto.request.CommentCreateRequest;
import com.example.bookmark.domain.record.dto.request.RecordCreateRequest;
import com.example.bookmark.domain.record.dto.response.RecordDetailResponse;
import com.example.bookmark.domain.record.dto.response.RecordSaveResponse;
import com.example.bookmark.domain.record.entity.*;
import com.example.bookmark.domain.record.entity.Record;
import com.example.bookmark.domain.record.exception.RecordErrorCode;
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

    // 1. 콜렉트북 기록 상세 조회
    public RecordDetailResponse getRecordDetail(Long userId, Long collectBookId, Long recordId) {
        CollectBook collectBook = collectBookRepository.findById(collectBookId)
                .orElseThrow(() -> new CustomException(CollectBookErrorCode.COLLECT_BOOK_NOT_FOUND));

        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new CustomException(RecordErrorCode.RECORD_NOT_FOUND));

        if (record.getChapter() == null || !record.getChapter().getCollectBook().getId().equals(collectBookId)) {
            throw new CustomException(RecordErrorCode.INVALID_RECORD_CHAPTER);
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

    // 2. 콜렉트북에 기록 생성 - 메모지 기반 X
    @Transactional
    public RecordSaveResponse createCustomRecord(Long userId, Long collectBookId, Long chapterId, RecordCreateRequest request) {
        CollectBook collectBook = collectBookRepository.findById(collectBookId)
                .orElseThrow(() -> new CustomException(CollectBookErrorCode.COLLECT_BOOK_NOT_FOUND));

        if (!collectBook.getUser().getId().equals(userId)) {
            throw new CustomException(CollectBookErrorCode.COLLECT_BOOK_FORBIDDEN);
        }

        Chapter targetChapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new CustomException(RecordErrorCode.CHAPTER_NOT_FOUND));

        return savePublishedRecord(userId, targetChapter, null, request);
    }

    // 3. 콜렉트북에 기록 생성 - 메모지 기반 O
    @Transactional
    public RecordSaveResponse createMemoRecord(Long userId, Long memoId, RecordCreateRequest request) {
        LocalDate now = LocalDate.now();
        Chapter systemChapter = findSystemMonthlyChapter(userId, now.getYear(), now.getMonthValue());

        BucketBoardMemo memo = bucketBoardMemoRepository.findById(memoId)
                .orElseThrow(() -> new CustomException(RecordErrorCode.MEMO_NOT_FOUND));

        return savePublishedRecord(userId, systemChapter, memo, request);
    }

    // 기록 발행 공통 메서드 (신규 저장 전용)
    private RecordSaveResponse savePublishedRecord(Long userId, Chapter chapter, BucketBoardMemo memo, RecordCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(RecordErrorCode.USER_NOT_FOUND));

        Record record = Record.builder()
                .user(user)
                .chapter(chapter)
                .bucketBoardMemo(memo)
                .title(request.getTitle())
                .content(request.getContent())
                .aiImageUrl(request.getAiImageUrl())
                .build();

        attachImagesAndKeywords(record, request.getImageUrls(), request.getKeywordIds());
        Record savedRecord = recordRepository.save(record);
        return RecordSaveResponse.from(savedRecord);
    }

    // 4. 댓글 작성
    @Transactional
    public void createComment(Long userId, Long collectBookId, Long recordId, CommentCreateRequest request) {
        Record record = validateRecordAndFriendship(userId, collectBookId, recordId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(RecordErrorCode.USER_NOT_FOUND));

        RecordComment comment = RecordComment.builder()
                .record(record)
                .user(user)
                .content(request.getContent())
                .build();

        recordCommentRepository.save(comment);
    }

    // 5. 좋아요 등록
    @Transactional
    public void addLike(Long userId, Long collectBookId, Long recordId) {
        Record record = validateRecordAndFriendship(userId, collectBookId, recordId);

        if (recordLikeRepository.existsByRecordIdAndUserId(recordId, userId)) {
            throw new CustomException(RecordErrorCode.LIKE_ALREADY_EXISTS);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(RecordErrorCode.USER_NOT_FOUND));

        recordLikeRepository.save(RecordLike.builder().record(record).user(user).build());
    }

    // 6. 좋아요 취소 (삭제)
    @Transactional
    public void deleteLike(Long userId, Long collectBookId, Long recordId) {
        validateRecordAndFriendship(userId, collectBookId, recordId);

        RecordLike recordLike = recordLikeRepository.findByRecordIdAndUserId(recordId, userId)
                .orElseThrow(() -> new CustomException(RecordErrorCode.LIKE_NOT_FOUND));

        recordLikeRepository.delete(recordLike);
    }

    // 7. AI 추천 감성 스크랩북 이미지 최종 저장 (PATCH)
    @Transactional
    public void updateAiImageUrl(Long userId, Long recordId, String aiImageUrl) {
        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new CustomException(RecordErrorCode.RECORD_NOT_FOUND));

        if (!record.getUser().getId().equals(userId)) {
            throw new CustomException(RecordErrorCode.RECORD_FORBIDDEN);
        }

        record.updateAiImageUrl(aiImageUrl);
    }

    // --- Helper 메서드 ---

    private Chapter findSystemMonthlyChapter(Long userId, int year, int month) {
        CollectBook systemCollectBook = collectBookRepository.findByUserIdAndYearAndCollectBookType(userId, year, CollectBookType.SYSTEM)
                .orElseThrow(() -> new CustomException(CollectBookErrorCode.COLLECT_BOOK_NOT_FOUND, year + "년도 시스템 자동 생성 콜렉트북을 찾을 수 없습니다."));

        return systemCollectBook.getChapters().stream()
                .filter(ch -> ch.getSequence() == month)
                .findFirst()
                .orElseThrow(() -> new CustomException(RecordErrorCode.CHAPTER_NOT_FOUND, month + "월 챕터를 찾을 수 없습니다."));
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

            if (keywords.size() != keywordIds.size()) {
                throw new CustomException(RecordErrorCode.KEYWORD_NOT_FOUND);
            }

            for (Keyword keyword : keywords) {
                record.addRecordKeyword(RecordKeyword.builder().keyword(keyword).build());
            }
        }
    }

    private boolean isFriend(Long userId, Long targetUserId) {
        if (userId.equals(targetUserId)) return true;
        return friendRepository.existsByUserIdAndFriendUserIdAndStatus(userId, targetUserId, FriendStatus.ACCEPTED)
                || friendRepository.existsByUserIdAndFriendUserIdAndStatus(targetUserId, userId, FriendStatus.ACCEPTED);
    }

    private void validateCollectBookVisibility(Long userId, CollectBook collectBook) {
        Long ownerId = collectBook.getUser().getId();
        Visibility visibility = collectBook.getVisibility();

        if (visibility == Visibility.PRIVATE && !ownerId.equals(userId)) {
            throw new CustomException(CollectBookErrorCode.PRIVATE_COLLECT_BOOK);
        }

        if (visibility == Visibility.FRIENDS && !isFriend(userId, ownerId)) {
            throw new CustomException(CollectBookErrorCode.FRIENDS_ONLY_COLLECT_BOOK);
        }
    }

    private Record validateRecordAndFriendship(Long userId, Long collectBookId, Long recordId) {
        CollectBook collectBook = collectBookRepository.findById(collectBookId)
                .orElseThrow(() -> new CustomException(CollectBookErrorCode.COLLECT_BOOK_NOT_FOUND));

        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new CustomException(RecordErrorCode.RECORD_NOT_FOUND));

        if (record.getChapter() == null || !record.getChapter().getCollectBook().getId().equals(collectBookId)) {
            throw new CustomException(RecordErrorCode.INVALID_RECORD_CHAPTER);
        }

        validateCollectBookVisibility(userId, collectBook);

        Long ownerId = record.getUser().getId();

        if (!isFriend(userId, ownerId)) {
            throw new CustomException(RecordErrorCode.FRIENDSHIP_REQUIRED);
        }

        return record;
    }
}