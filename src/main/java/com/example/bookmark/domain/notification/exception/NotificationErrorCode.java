package com.example.bookmark.domain.notification.exception;

import com.example.bookmark.common.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum NotificationErrorCode implements BaseCode {

    // 404 NOT_FOUND
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION404_1", "존재하지 않는 알림입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION404_2", "존재하지 않는 사용자입니다."),

    // 403 FORBIDDEN
    NOTIFICATION_FORBIDDEN(HttpStatus.FORBIDDEN, "NOTIFICATION403_1", "해당 알림에 대한 접근 권한이 없습니다."),

    // 500 INTERNAL_SERVER_ERROR
    NOTIFICATION_CONNECT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "NOTIFICATION500_1", "실시간 알림 연결(SSE) 생성 중 에러가 발생했습니다."),
    NOTIFICATION_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "NOTIFICATION500_2", "알림 전파 과정에서 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}