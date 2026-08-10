package com.example.bookmark.domain.friend.service;

import com.example.bookmark.domain.friend.dto.response.FriendRequestResponse;
import com.example.bookmark.domain.friend.dto.response.UserSummaryResponse;
import com.example.bookmark.domain.friend.entity.Friendship;
import com.example.bookmark.domain.friend.entity.enums.FriendStatus;
import com.example.bookmark.domain.friend.repository.FriendRepository;
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
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    // 이름으로 유저 검색 (본인 제외)
    public List<UserSummaryResponse> searchByName(Long meId, String name) {
        return userRepository.findByNameContainingIgnoreCase(name).stream()
                .filter(user -> !user.getId().equals(meId))
                .map(UserSummaryResponse::from)
                .toList();
    }

    // 친구 신청
    @Transactional
    public UserSummaryResponse addFriend(Long meId, Long friendUserId) {
        if (meId.equals(friendUserId)) {
            throw new IllegalArgumentException("자기 자신에게는 신청할 수 없습니다.");
        }

        User friend = userRepository.findById(friendUserId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 어느 방향이든 이미 관계가 있으면 중복 신청 방지
        if (friendRepository.existsByUserIdAndFriendUserId(meId, friendUserId)
                || friendRepository.existsByUserIdAndFriendUserId(friendUserId, meId)) {
            throw new IllegalArgumentException("이미 신청했거나 친구인 회원입니다.");
        }

        friendRepository.save(
                Friendship.builder()
                        .userId(meId)
                        .friendUserId(friendUserId)
                        .build()
        );

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
            throw new IllegalArgumentException("친구가 아닙니다.");
        }
        friendRepository.deleteByUserIdAndFriendUserId(meId, friendUserId);
        friendRepository.deleteByUserIdAndFriendUserId(friendUserId, meId);
    }

    // 받은 친구 신청 목록 (알림)
    public List<FriendRequestResponse> getReceivedRequests(Long meId) {
        return friendRepository.findAllByFriendUserIdAndStatus(meId, FriendStatus.PENDING).stream()
                .map(req -> {
                    User sender = userRepository.findById(req.getUserId())
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
                    return FriendRequestResponse.of(req.getId(), sender);
                })
                .toList();
    }

    // 친구 신청 수락
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
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 친구 신청입니다."));
        if (!request.getFriendUserId().equals(meId)) {
            throw new IllegalStateException("처리 권한이 없는 친구 신청입니다.");
        }
        if (request.getStatus() != FriendStatus.PENDING) {
            throw new IllegalArgumentException("이미 처리된 친구 신청입니다.");
        }
        return request;
    }
}