package com.example.bookmark.domain.friend.service;

import com.example.bookmark.common.exception.CustomException;
import com.example.bookmark.domain.collectBook.entity.CollectBook;
import com.example.bookmark.domain.collectBook.entity.enums.Visibility;
import com.example.bookmark.domain.collectBook.repository.CollectBookRepository;
import com.example.bookmark.domain.friend.dto.response.FriendPageResponse;
import com.example.bookmark.domain.friend.dto.response.FriendRequestResponse;
import com.example.bookmark.domain.friend.dto.response.UserSummaryResponse;
import com.example.bookmark.domain.friend.entity.Friendship;
import com.example.bookmark.domain.friend.entity.enums.FriendStatus;
import com.example.bookmark.domain.friend.exception.FriendErrorCode;
import com.example.bookmark.domain.friend.repository.FriendRepository;
import com.example.bookmark.domain.notification.entity.enums.NotificationType;
import com.example.bookmark.domain.notification.service.NotificationService;
import com.example.bookmark.domain.user.entity.User;
import com.example.bookmark.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final CollectBookRepository collectBookRepository;
    private final NotificationService notificationService;

    // 친구 페이지 (친구 프로필 + 공개해둔 책)
    public FriendPageResponse getFriendPage(Long meId, Long friendUserId) {
        // 실제 친구인지 확인 (수락된 양방향 관계)
        boolean isFriend = friendRepository.existsByUserIdAndFriendUserIdAndStatus(meId, friendUserId, FriendStatus.ACCEPTED)
                || friendRepository.existsByUserIdAndFriendUserIdAndStatus(friendUserId, meId, FriendStatus.ACCEPTED);
        if (!isFriend) {
            throw new CustomException(FriendErrorCode.FRIEND_PAGE_FORBIDDEN);
        }

        User friend = userRepository.findById(friendUserId)
                .orElseThrow(() -> new CustomException(FriendErrorCode.FRIEND_NOT_FOUND));

        // 친구가 공개한 책: 전체공개 + 친구공개 (비공개 제외)
        List<CollectBook> books = collectBookRepository.findByUserIdAndVisibilityInOrderByYearDesc(
                friendUserId, List.of(Visibility.PUBLIC, Visibility.FRIENDS));

        return FriendPageResponse.of(friend, books);
    }

    // 이름으로 유저 검색 (본인 제외)
    public List<UserSummaryResponse> searchByName(Long meId, String name) {
        return userRepository.findByNameContainingIgnoreCase(name).stream()
                .filter(user -> !user.getId().equals(meId))
                .map(UserSummaryResponse::from)
                .toList();
    }

    // 친구 신청 (예외 검증 및 알림 발송 포함)
    @Transactional
    public UserSummaryResponse addFriend(Long meId, Long friendUserId) {
        // 예외 1: 자기 자신에게 친구 신청을 보낸 경우 (400)
        if (meId.equals(friendUserId)) {
            throw new CustomException(FriendErrorCode.CANNOT_REQUEST_SELF);
        }

        // 예외 2: 본인 또는 신청 대상 회원이 DB에 존재하지 않는 경우 (404)
        User me = userRepository.findById(meId)
                .orElseThrow(() -> new CustomException(FriendErrorCode.FRIEND_NOT_FOUND));
        User friend = userRepository.findById(friendUserId)
                .orElseThrow(() -> new CustomException(FriendErrorCode.FRIEND_NOT_FOUND));

        // 예외 3 & 4: 이미 신청했거나, 이미 친구이거나, 상대방이 나에게 신청을 보낸 상태인 경우 (400)
        if (friendRepository.existsByUserIdAndFriendUserId(meId, friendUserId)
                || friendRepository.existsByUserIdAndFriendUserId(friendUserId, meId)) {
            throw new CustomException(FriendErrorCode.ALREADY_PROCESSING_OR_FRIEND);
        }

        // 친구 신청 DB 저장
        friendRepository.save(
                Friendship.builder()
                        .userId(meId)
                        .friendUserId(friendUserId)
                        .build()
        );

        // 예외 5 격리: 알림 전송 에러 시 메인 트랜잭션 롤백 방지
        try {
            notificationService.send(
                    friend,
                    NotificationType.FRIEND_REQUEST,
                    me.getName(),
                    me.getName() + "님이 친구 신청을 보냈어요.",
                    "/friends/requests"
            );
        } catch (Exception e) {
            log.error("친구 신청 알림 전송 실패 - senderId: {}, receiverId: {}, error: {}", meId, friendUserId, e.getMessage());
        }

        return UserSummaryResponse.from(friend);
    }

    // 친구 목록 (수락된 관계, 양방향)
    public List<UserSummaryResponse> getFriends(Long meId) {
        List<Long> friendIds = new ArrayList<>();
        friendRepository.findAllByUserIdAndStatus(meId, FriendStatus.ACCEPTED)
                .forEach(f -> friendIds.add(f.getFriendUserId()));
        friendRepository.findAllByFriendUserIdAndStatus(meId, FriendStatus.ACCEPTED)
                .forEach(f -> friendIds.add(f.getUserId()));

        return userRepository.findAllById(friendIds).stream()
                .map(UserSummaryResponse::from)
                .toList();
    }

    // 친구 삭제 (양방향 어느 쪽 행이든 제거)
    @Transactional
    public void deleteFriend(Long meId, Long friendUserId) {
        boolean exists = friendRepository.existsByUserIdAndFriendUserId(meId, friendUserId)
                || friendRepository.existsByUserIdAndFriendUserId(friendUserId, meId);
        if (!exists) {
            throw new CustomException(FriendErrorCode.NOT_FRIEND);
        }
        friendRepository.deleteByUserIdAndFriendUserId(meId, friendUserId);
        friendRepository.deleteByUserIdAndFriendUserId(friendUserId, meId);
    }

    // 받은 친구 신청 목록 (알림)
    public List<FriendRequestResponse> getReceivedRequests(Long meId) {
        return friendRepository.findAllByFriendUserIdAndStatus(meId, FriendStatus.PENDING).stream()
                .map(req -> {
                    User sender = userRepository.findById(req.getUserId())
                            .orElseThrow(() -> new CustomException(FriendErrorCode.FRIEND_NOT_FOUND));
                    return FriendRequestResponse.of(req.getId(), sender);
                })
                .toList();
    }

    // 친구 신청 수락 (알림 발송 없음)
    @Transactional
    public void acceptRequest(Long meId, Long requestId) {
        findPendingRequestForMe(meId, requestId).accept();
    }

    // 친구 신청 거절
    @Transactional
    public void rejectRequest(Long meId, Long requestId) {
        friendRepository.delete(findPendingRequestForMe(meId, requestId));
    }

    private Friendship findPendingRequestForMe(Long meId, Long requestId) {
        Friendship request = friendRepository.findById(requestId)
                .orElseThrow(() -> new CustomException(FriendErrorCode.FRIEND_REQUEST_NOT_FOUND));
        if (!request.getFriendUserId().equals(meId)) {
            throw new CustomException(FriendErrorCode.FRIEND_REQUEST_FORBIDDEN);
        }
        if (request.getStatus() != FriendStatus.PENDING) {
            throw new CustomException(FriendErrorCode.ALREADY_PROCESSED_REQUEST);
        }
        return request;
    }
}