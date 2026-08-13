package com.example.bookmark.domain.notification.dto.response;

import com.example.bookmark.domain.notification.entity.Notification;
import com.example.bookmark.domain.notification.entity.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponse {

    private Long id;
    private NotificationType type;
    private String senderName;
    private String content;
    private String redirectUrl;
    private boolean isRead;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .senderName(notification.getSenderName())
                .content(notification.getContent())
                .redirectUrl(notification.getRedirectUrl())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}