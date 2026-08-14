package com.example.bookmark.domain.notification.controller;

import com.example.bookmark.common.response.ApiResponse;
import com.example.bookmark.common.response.PageResponse;
import com.example.bookmark.domain.notification.dto.request.NotificationSettingRequest;
import com.example.bookmark.domain.notification.dto.response.NotificationResponse;
import com.example.bookmark.domain.notification.dto.response.UnreadCountResponse;
import com.example.bookmark.domain.notification.service.NotificationService;
import com.example.bookmark.global.auth.LoginUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "Notification", description = "알림 API")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 1. 실시간 SSE 알림 구독 (produces = text/event-stream)
    @Operation(
            summary = "실시간 SSE 알림 구독",
            description = "클라이언트가 서버와 SSE(Server-Sent Events) 연결을 맺고 실시간 알림을 수신 대기합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "SSE 연결 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "SSE 연결 실패", content = @Content)
    })
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @Parameter(hidden = true) @LoginUserId Long userId
    ) {
        return notificationService.subscribe(userId);
    }

    // 2. 알림 목록 조회 (category: ALL, FRIEND, LIKE, COMMENT)
    @Operation(
            summary = "알림 목록 페이징 조회",
            description = "사용자의 알림 목록을 카테고리별(ALL, FRIEND, LIKE, COMMENT)로 최신순 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "알림 목록 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
    })
    @GetMapping
    public ApiResponse<PageResponse<NotificationResponse>> getNotifications(
            @Parameter(hidden = true) @LoginUserId Long userId,
            @Parameter(description = "알림 카테고리 (ALL, FRIEND, LIKE, COMMENT)", example = "ALL")
            @RequestParam(required = false, defaultValue = "ALL") String category,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        PageResponse<NotificationResponse> response = notificationService.getNotifications(userId, category, pageable);
        return ApiResponse.onSuccess(response);
    }

    // 3. 읽지 않은 알림 개수 조회 (상단 종 아이콘 뱃지용)
    @Operation(
            summary = "읽지 않은 알림 개수 조회",
            description = "상단 헤더의 종 아이콘 뱃지에 표시할 읽지 않은 알림의 개수를 반환합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "안읽은 알림 개수 조회 성공")
    })
    @GetMapping("/unread-count")
    public ApiResponse<UnreadCountResponse> getUnreadCount(
            @Parameter(hidden = true) @LoginUserId Long userId
    ) {
        UnreadCountResponse response = notificationService.getUnreadCount(userId);
        return ApiResponse.onSuccess(response);
    }

    // 4. 단건 알림 읽음 처리
    @Operation(
            summary = "단건 알림 읽음 처리",
            description = "특정 알림 1개를 읽음(isRead = true) 상태로 변경합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "알림 읽음 처리 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 알림에 대한 접근 권한 없음", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 알림 ID", content = @Content)
    })
    @PatchMapping("/{notificationId}/read")
    public ApiResponse<String> readNotification(
            @Parameter(hidden = true) @LoginUserId Long userId,
            @Parameter(description = "읽음 처리할 알림 ID", example = "10") @PathVariable Long notificationId
    ) {
        notificationService.readNotification(userId, notificationId);
        return ApiResponse.onSuccess("알림을 읽음 처리했습니다.");
    }

    // 5. 전체 알림 읽음 처리
    @Operation(
            summary = "전체 알림 읽음 처리",
            description = "사용자의 읽지 않은 모든 알림을 한 번에 읽음 상태로 변경합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "모든 알림 읽음 처리 성공")
    })
    @PatchMapping("/read-all")
    public ApiResponse<String> readAllNotifications(
            @Parameter(hidden = true) @LoginUserId Long userId
    ) {
        notificationService.readAllNotifications(userId);
        return ApiResponse.onSuccess("모든 알림을 읽음 처리했습니다.");
    }

    // 6. 알림 설정 변경 API (토글용)
    @Operation(
            summary = "인앱 알림 ON/OFF 설정 변경",
            description = "사용자의 인앱 알림 수신 여부 설정(true/false)을 변경합니다."
    )
    @PatchMapping("/settings")
    public ApiResponse<Boolean> updateNotificationSetting(
            @Parameter(hidden = true) @LoginUserId Long userId,
            @Valid @RequestBody NotificationSettingRequest request
    ) {
        boolean result = notificationService.updateNotificationSetting(userId, request.enabled());
        return ApiResponse.onSuccess(result);
    }
}