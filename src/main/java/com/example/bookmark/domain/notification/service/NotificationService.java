package com.example.bookmark.domain.notification.service;

import com.example.bookmark.common.exception.CustomException;
import com.example.bookmark.common.response.PageResponse;
import com.example.bookmark.domain.notification.dto.response.NotificationResponse;
import com.example.bookmark.domain.notification.dto.response.UnreadCountResponse;
import com.example.bookmark.domain.notification.entity.Notification;
import com.example.bookmark.domain.notification.entity.enums.NotificationType;
import com.example.bookmark.domain.notification.exception.NotificationErrorCode;
import com.example.bookmark.domain.notification.repository.NotificationRepository;
import com.example.bookmark.domain.user.entity.User;
import com.example.bookmark.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final UserRepository userRepository;

    // 1. SSE 구독
    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(60 * 1000L * 60);
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));

        try {
            emitter.send(SseEmitter.event().name("connect").data("Connected!"));
        } catch (IOException e) {
            emitters.remove(userId);
            throw new CustomException(NotificationErrorCode.NOTIFICATION_CONNECT_ERROR);
        }

        return emitter;
    }

    // 2. 알림 발송
    @Transactional
    public void send(User receiver, NotificationType type, String senderName, String content, String redirectUrl) {
        // 1. DB에 알림 내역 저장
        Notification notification = notificationRepository.save(
                Notification.builder()
                        .receiver(receiver)
                        .type(type)
                        .senderName(senderName)
                        .content(content)
                        .redirectUrl(redirectUrl)
                        .build()
        );

        // 2. SSE 실시간 발송 조건:
        // - 알림 설정이 ON(true)이거나
        // - 알림 설정이 OFF(false)이더라도 친구 요청(FRIEND_REQUEST)인 경우!
        Boolean shouldSendSse = receiver.getIsInAppNotificationEnabled()
                || type == NotificationType.FRIEND_REQUEST;

        if (shouldSendSse) {
            SseEmitter emitter = emitters.get(receiver.getId());
            if (emitter != null) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("notification")
                            .data(NotificationResponse.from(notification)));
                } catch (IOException e) {
                    emitters.remove(receiver.getId());
                }
            }
        }
    }

    // 3. 알림 목록 페이징 조회
    public PageResponse<NotificationResponse> getNotifications(Long userId, String category, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NotificationErrorCode.USER_NOT_FOUND));

        Page<Notification> page;

        // 알림 설정이 OFF(false)인 유저는 카테고리에 상관없이 오직 'FRIEND_REQUEST' 알림만 반환
        if (!user.getIsInAppNotificationEnabled()) {
            page = notificationRepository.findByReceiverIdAndTypeInOrderByCreatedAtDesc(
                    userId, List.of(NotificationType.FRIEND_REQUEST), pageable);
        } else {
            // 알림 설정이 ON(true)인 경우 카테고리별 정밀 조회
            if ("FRIEND".equalsIgnoreCase(category)) {
                page = notificationRepository.findByReceiverIdAndTypeInOrderByCreatedAtDesc(
                        userId, List.of(NotificationType.FRIEND_REQUEST), pageable);
            } else if ("LIKE".equalsIgnoreCase(category)) {
                page = notificationRepository.findByReceiverIdAndTypeInOrderByCreatedAtDesc(
                        userId, List.of(NotificationType.LIKE), pageable);
            } else if ("COMMENT".equalsIgnoreCase(category)) {
                page = notificationRepository.findByReceiverIdAndTypeInOrderByCreatedAtDesc(
                        userId, List.of(NotificationType.COMMENT), pageable);
            } else {
                // ALL 혹은 기타 카테고리
                page = notificationRepository.findByReceiverIdOrderByCreatedAtDesc(userId, pageable);
            }
        }

        Page<NotificationResponse> dtoPage = page.map(NotificationResponse::from);
        return PageResponse.from(dtoPage);
    }

    // 4. 읽지 않은 알림 개수 조회
    public UnreadCountResponse getUnreadCount(Long userId) {
        long count = notificationRepository.countByReceiverIdAndIsReadFalse(userId);
        return new UnreadCountResponse(count);
    }

    // 5. 단건 알림 읽음 처리
    @Transactional
    public void readNotification(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.getReceiver().getId().equals(userId)) {
            throw new CustomException(NotificationErrorCode.NOTIFICATION_FORBIDDEN);
        }

        notification.markAsRead();
    }

    // 6. 전체 알림 읽음 처리
    @Transactional
    public void readAllNotifications(Long userId) {
        notificationRepository.markAllAsReadByReceiverId(userId);
    }

    // 7. 인앱 알림 ON/OFF 설정 변경
    @Transactional
    public boolean updateNotificationSetting(Long userId, boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NotificationErrorCode.USER_NOT_FOUND));

        user.updateInAppNotificationSetting(enabled);

        return user.getIsInAppNotificationEnabled();
    }
}