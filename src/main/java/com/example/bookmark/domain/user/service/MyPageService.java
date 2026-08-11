package com.example.bookmark.domain.user.service;

import com.example.bookmark.domain.collectBook.entity.CollectBook;
import com.example.bookmark.domain.collectBook.entity.enums.Visibility;
import com.example.bookmark.domain.collectBook.repository.CollectBookRepository;
import com.example.bookmark.domain.friend.entity.enums.FriendStatus;
import com.example.bookmark.domain.friend.repository.FriendRepository;
import com.example.bookmark.domain.user.dto.response.MyPageResponse;
import com.example.bookmark.domain.user.entity.User;
import com.example.bookmark.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final UserRepository userRepository;
    private final CollectBookRepository collectBookRepository;
    private final FriendRepository friendRepository;

    public MyPageResponse getMyPage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 프로필
        MyPageResponse.Profile profile = new MyPageResponse.Profile(
                user.getId(), user.getName(), user.getLoginId(),
                user.getBio(), user.getProfileImageUrl()
        );

        // 집계
        long totalRecords = 0L;      // TODO: 버킷/기록 도메인 구현 후 연결
        long completedBuckets = 0L;  // TODO
        long collectBookCount = collectBookRepository.countByUserId(userId);
        long friendCount = friendRepository.countByUserIdAndStatus(userId, FriendStatus.ACCEPTED)
                + friendRepository.countByFriendUserIdAndStatus(userId, FriendStatus.ACCEPTED);

        MyPageResponse.Stats stats = new MyPageResponse.Stats(
                totalRecords, completedBuckets, collectBookCount, friendCount
        );

        // 최근 기록한 책 5개 (본인이므로 전부)
        List<MyPageResponse.RecentBook> recentBooks =
                collectBookRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId).stream()
                        .map(book -> new MyPageResponse.RecentBook(
                                book.getId(), book.getYear(), book.getTitle(),
                                book.getBookColor(), book.getVisibility()
                        ))
                        .toList();

        // 친구 목록 (수락된 관계, 양방향)
        List<Long> friendIds = new ArrayList<>();
        friendRepository.findAllByUserIdAndStatus(userId, FriendStatus.ACCEPTED)
                .forEach(f -> friendIds.add(f.getFriendUserId()));
        friendRepository.findAllByFriendUserIdAndStatus(userId, FriendStatus.ACCEPTED)
                .forEach(f -> friendIds.add(f.getUserId()));

        List<MyPageResponse.Friend> friends = userRepository.findAllById(friendIds).stream()
                .map(f -> new MyPageResponse.Friend(
                        f.getId(), f.getName(), f.getLoginId(), f.getProfileImageUrl()
                ))
                .toList();

        return new MyPageResponse(profile, stats, recentBooks, friends);
    }
}