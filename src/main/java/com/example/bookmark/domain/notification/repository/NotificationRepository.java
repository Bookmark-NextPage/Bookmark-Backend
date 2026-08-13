package com.example.bookmark.domain.notification.repository;

import com.example.bookmark.domain.notification.entity.Notification;
import com.example.bookmark.domain.notification.entity.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 특정 사용자의 카테고리별 알림 페이징 조회
    Page<Notification> findByReceiverIdAndTypeInOrderByCreatedAtDesc(
            Long receiverId, List<NotificationType> types, Pageable pageable);

    // 전체 카테고리 페이징 조회
    Page<Notification> findByReceiverIdOrderByCreatedAtDesc(Long receiverId, Pageable pageable);

    // 읽지 않은 알림 수 카운트
    long countByReceiverIdAndIsReadFalse(Long receiverId);

    // 모두 읽음 처리
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.receiver.id = :receiverId AND n.isRead = false")
    void markAllAsReadByReceiverId(@Param("receiverId") Long receiverId);
}