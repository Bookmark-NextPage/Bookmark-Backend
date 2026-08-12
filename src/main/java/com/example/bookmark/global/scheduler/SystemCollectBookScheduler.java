package com.example.bookmark.global.scheduler;

import com.example.bookmark.domain.collectBook.service.SystemCollectBookService;
import com.example.bookmark.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class SystemCollectBookScheduler {

    private final UserRepository userRepository;
    private final SystemCollectBookService systemCollectBookService;

    /**
     * 매년 1월 1일 00:00에 모든 유저의 시스템 콜렉트북 생성
     */
    @Scheduled(cron = "0 0 0 1 1 *")
    public void createNewYearCollectBooks() {

        int year = LocalDate.now().getYear();

        log.info("{}년도 시스템 콜렉트북 자동 생성 시작", year);

        userRepository.findAll()
                .forEach(user ->
                        systemCollectBookService.createSystemCollectBook(user, year)
                );

        log.info("{}년도 시스템 콜렉트북 자동 생성 완료", year);
    }
}