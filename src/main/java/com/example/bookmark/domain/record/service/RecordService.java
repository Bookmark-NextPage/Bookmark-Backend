package com.example.bookmark.domain.record.service;

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

    // 1. 임시 저장
    @Transactional
    public RecordSaveResponse saveDraft(RecordDraftSaveRequest request) {
        Record record;

        if (request.getRecordId() != null) {
            record = recordRepository.findById(request.getRecordId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 임시저장 기록입니다. id=" + request.getRecordId()));

            record.updateRecord(record.getChapter(), request.getTitle(), request.getContent(), RecordStatus.DRAFT);
            record.getImages().clear();
            record.getRecordKeywords().clear();
        } else {
            record = Record.builder()
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
    public RecordSaveResponse getLatestDraft() {
        Record draftRecord = recordRepository.findFirstByStatusOrderByCreatedAtDesc(RecordStatus.DRAFT)
                .orElse(null);

        return draftRecord != null ? RecordSaveResponse.from(draftRecord) : null;
    }

    // 3. 콜렉트북에 기록 생성 - 메모지 기반 X
    @Transactional
    public RecordSaveResponse createCustomRecord(Long collectBookId, Long chapterId, RecordCreateRequest request) {
        Chapter targetChapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 챕터입니다. id=" + chapterId));

        return savePublishedRecord(targetChapter, null, request);
    }

    // 4. 콜렉트북에 기록 생성 - 메모지 기반 O
    @Transactional
    public RecordSaveResponse createMemoRecord(Long memoId, RecordCreateRequest request) {
        LocalDate now = LocalDate.now();
        Chapter systemChapter = findSystemMonthlyChapter(now.getYear(), now.getMonthValue());

        return savePublishedRecord(systemChapter, memoId, request);
    }

    // 공통 최종 저장 메서드
    private RecordSaveResponse savePublishedRecord(Chapter chapter, Long memoId, RecordCreateRequest request) {
        Record record;

        if (request.getRecordId() != null) {
            record = recordRepository.findById(request.getRecordId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기록입니다. id=" + request.getRecordId()));

            record.updateRecord(chapter, request.getTitle(), request.getContent(), RecordStatus.PUBLISHED);
            record.getImages().clear();
            record.getRecordKeywords().clear();
        } else {
            record = Record.builder()
                    .chapter(chapter)
                    .memoId(memoId)
                    .title(request.getTitle())
                    .content(request.getContent())
                    .status(RecordStatus.PUBLISHED)
                    .build();
        }

        attachImagesAndKeywords(record, request.getImageUrls(), request.getKeywordIds());
        Record savedRecord = recordRepository.save(record);
        return RecordSaveResponse.from(savedRecord);
    }

    private Chapter findSystemMonthlyChapter(int year, int month) {
        CollectBook systemCollectBook = collectBookRepository.findByYearAndCollectBookType(year, CollectBookType.SYSTEM)
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