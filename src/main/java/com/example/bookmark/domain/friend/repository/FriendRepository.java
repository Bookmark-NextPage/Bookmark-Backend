package com.example.bookmark.domain.friend.repository;

import com.example.bookmark.domain.friend.entity.Friendship;
import com.example.bookmark.domain.friend.entity.enums.FriendStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friendship, Long> {

    boolean existsByUserIdAndFriendUserId(Long userId, Long friendUserId);
    boolean existsByUserIdAndFriendUserIdAndStatus(Long userId, Long friendUserId, FriendStatus status);

    // 받은 신청(알림) / 내가 받은 쪽의 수락된 친구
    List<Friendship> findAllByFriendUserIdAndStatus(Long friendUserId, FriendStatus status);

    // 내가 보낸 쪽의 수락된 친구
    List<Friendship> findAllByUserIdAndStatus(Long userId, FriendStatus status);

    long countByUserIdAndStatus(Long userId, FriendStatus status);
    long countByFriendUserIdAndStatus(Long friendUserId, FriendStatus status);

    void deleteByUserIdAndFriendUserId(Long userId, Long friendUserId);

    // 회원 탈퇴 시 정리
    void deleteAllByUserIdOrFriendUserId(Long userId, Long friendUserId);
}