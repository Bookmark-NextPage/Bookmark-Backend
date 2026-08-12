package com.example.bookmark.domain.collectBook.service;

import com.example.bookmark.domain.collectBook.entity.Chapter;
import com.example.bookmark.domain.collectBook.entity.CollectBook;
import com.example.bookmark.domain.collectBook.entity.enums.BookColor;
import com.example.bookmark.domain.collectBook.entity.enums.ChapterType;
import com.example.bookmark.domain.collectBook.entity.enums.CollectBookType;
import com.example.bookmark.domain.collectBook.entity.enums.Visibility;
import com.example.bookmark.domain.collectBook.repository.CollectBookRepository;
import com.example.bookmark.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemCollectBookService {

    private final CollectBookRepository collectBookRepository;

    /**
     * 특정 유저에게 해당 연도의 시스템 콜렉트북(1~12월 챕터 포함)을 생성합니다.
     */
    @Transactional
    public void createSystemCollectBook(User user, int year) {
        // 유저당 연도별 1개 제한 중복 검증 (이미 존재하면 스킵)
        boolean exists = collectBookRepository.existsByUserIdAndYearAndCollectBookType(
                user.getId(), year, CollectBookType.SYSTEM
        );

        if (exists) {
            log.info("유저 ID: {} 에 대해 {}년도 시스템 콜렉트북이 이미 존재합니다.", user.getId(), year);
            return;
        }

        // 1. 시스템 콜렉트북 엔티티 생성
        CollectBook systemCollectBook = CollectBook.builder()
                .user(user)
                .title(year + "년")
                .bookColor(BookColor.PINK)
                .year(year)
                .visibility(Visibility.PUBLIC)
                .chapterType(ChapterType.MONTHLY)
                .chapterNum(12)
                .collectBookType(CollectBookType.SYSTEM)
                .build();

        // 2. 1월~12월 챕터(MONTHLY) 생성 및 콜렉트북에 추가
        for (int month = 1; month <= 12; month++) {
            Chapter monthlyChapter = Chapter.builder()
                    .name(month + "월")
                    .sequence(month)
                    .build();
            systemCollectBook.addChapter(monthlyChapter);
        }

        collectBookRepository.save(systemCollectBook);
        log.info("유저 ID: {} 에 대해 {}년도 시스템 콜렉트북 생성 완료", user.getId(), year);
    }
}