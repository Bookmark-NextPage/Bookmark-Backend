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

    // viewerId: 지금 보고 있는 사람 / userId: 조회 대상 페이지의 주인
    public MyPageResponse getMyPage(Long viewerId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 프로필
        MyPageResponse.Profile profile = new MyPageResponse.Profile(
                user.getId(), user.getName(), user.getLoginId(),
                user.getBio(), user.getProfileImageUrl()
        );

        // 집계
        // TODO: 버킷보드/기록 도메인 구현 후 실제 값으로 교체
        long totalRecords = 0L;
        long completedBuckets = 0L;
        long collectBookCount = collectBookRepository.countByUserId(userId);
        long friendCount = friendRepository.countByUserIdAndStatus(userId, FriendStatus.ACCEPTED)
                + friendRepository.countByFriendUserIdAndStatus(userId, FriendStatus.ACCEPTED);

        MyPageResponse.Stats stats = new MyPageResponse.Stats(
                totalRecords, completedBuckets, collectBookCount, friendCount
        );

        // 최근 기록한 책 5개 (조회자와의 관계에 따라 공개 범위 필터링)
        List<CollectBook> books;
        if (viewerId.equals(userId)) {
            // 본인: 전부
            books = collectBookRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId);
        } else {
            boolean isFriend = isFriend(viewerId, userId);
            List<Visibility> visibilities = isFriend
                    ? List.of(Visibility.PUBLIC, Visibility.FRIENDS) // 친구: 전체공개 + 친구공개
                    : List.of(Visibility.PUBLIC);                    // 남: 전체공개만
            books = collectBookRepository
                    .findTop5ByUserIdAndVisibilityInOrderByCreatedAtDesc(userId, visibilities);
        }

        List<MyPageResponse.RecentBook> recentBooks = books.stream()
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

    // 두 사람이 수락된 친구인지 (양방향)
    private boolean isFriend(Long aId, Long bId) {
        return friendRepository.existsByUserIdAndFriendUserIdAndStatus(aId, bId, FriendStatus.ACCEPTED)
                || friendRepository.existsByUserIdAndFriendUserIdAndStatus(bId, aId, FriendStatus.ACCEPTED);
    }
}