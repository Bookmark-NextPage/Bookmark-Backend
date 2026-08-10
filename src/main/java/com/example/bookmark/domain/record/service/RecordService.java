package com.example.bookmark.domain.record.service;

import com.example.bookmark.domain.bucketBoard.entity.BucketBoardMemo;
import com.example.bookmark.domain.bucketBoard.repository.BucketBoardMemoRepository;
import com.example.bookmark.domain.collectBook.entity.Chapter;
import com.example.bookmark.domain.collectBook.entity.CollectBook;
import com.example.bookmark.domain.collectBook.entity.enums.CollectBookType;
import com.example.bookmark.domain.collectBook.repository.ChapterRepository;
import com.example.bookmark.domain.collectBook.repository.CollectBookRepository;
import com.example.bookmark.domain.record.dto.request.RecordCreateRequest;
import com.example.bookmark.domain.record.dto.request.RecordDraftSaveRequest;
import com.example.bookmark.domain.record.dto.response.RecordSaveResponse;
import com.example.bookmark.domain.record.entity.Keyword;
import com.example.bookmark.domain.record.entity.Record;
import com.example.bookmark.domain.record.entity.RecordImage;
import com.example.bookmark.domain.record.entity.RecordKeyword;
import com.example.bookmark.domain.record.entity.enums.RecordStatus;
import com.example.bookmark.domain.record.repository.KeywordRepository;
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

    // 1. 임시 저장
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
            record.getImages().clear();
            record.getRecordKeywords().clear();
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

    // 2. 최근 임시 저장 조회
    public RecordSaveResponse getLatestDraft(Long userId) {
        Record draftRecord = recordRepository.findFirstByUserIdAndStatusOrderByCreatedAtDesc(userId, RecordStatus.DRAFT)
                .orElse(null);

        return draftRecord != null ? RecordSaveResponse.from(draftRecord) : null;
    }

    // 3. 콜렉트북에 기록 생성 - 메모지 기반 X
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

    // 4. 콜렉트북에 기록 생성 - 메모지 기반 O
    @Transactional
    public RecordSaveResponse createMemoRecord(Long userId, Long memoId, RecordCreateRequest request) {
        LocalDate now = LocalDate.now();
        Chapter systemChapter = findSystemMonthlyChapter(userId, now.getYear(), now.getMonthValue());

        BucketBoardMemo memo = bucketBoardMemoRepository.findById(memoId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 버킷보드 메모입니다. id=" + memoId));

        return savePublishedRecord(userId, systemChapter, memo, request);
    }

    // 공통 최종 저장 메서드
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
            record.getImages().clear();
            record.getRecordKeywords().clear();
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
                record.getRecordKeywords().add(RecordKeyword.builder().record(record).keyword(keyword).build());
            }
        }
    }
}