package com.example.bookmark.domain.home.service;

import com.example.bookmark.common.exception.CustomException;
import com.example.bookmark.common.response.ErrorCode;
import com.example.bookmark.domain.bucketBoard.entity.enums.MemoState;
import com.example.bookmark.domain.bucketBoard.repository.BucketBoardMemoRepository;
import com.example.bookmark.domain.collectBook.repository.CollectBookRepository;
import com.example.bookmark.domain.home.dto.response.HomeResponse;
import com.example.bookmark.domain.home.dto.response.RecentRecordResponse;
import com.example.bookmark.domain.record.repository.RecordRepository;
import com.example.bookmark.domain.user.entity.User;
import com.example.bookmark.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeService {

    private final UserRepository userRepository;
    private final BucketBoardMemoRepository bucketBoardMemoRepository;
    private final CollectBookRepository collectBookRepository;
    private final RecordRepository recordRepository;

    public HomeResponse getHome(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        LocalDateTime startOfWeek = LocalDate.now()
                .with(DayOfWeek.MONDAY)
                .atStartOfDay();

        long weeklyCompletedMemoCount =
                bucketBoardMemoRepository
                        .countByUser_IdAndStateAndUpdatedAtGreaterThanEqual(
                                userId,
                                MemoState.COMPLETE,
                                startOfWeek
                        );

        long planMemoCount = bucketBoardMemoRepository
                .countByUser_IdAndState(userId, MemoState.PLAN);

        long collectBookCount = collectBookRepository.countByUser_Id(userId);

        long recordCount = recordRepository.countByUser_Id(userId);

        List<RecentRecordResponse> recentRecords = recordRepository
                .findTop3ByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(RecentRecordResponse::from)
                .toList();

        return new HomeResponse(
                user.getName(),
                weeklyCompletedMemoCount,
                planMemoCount,
                collectBookCount,
                recordCount,
                recentRecords
        );
    }
}
