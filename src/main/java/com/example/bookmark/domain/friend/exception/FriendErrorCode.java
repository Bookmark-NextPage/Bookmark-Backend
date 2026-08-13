package com.example.bookmark.domain.friend.exception;

import com.example.bookmark.common.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FriendErrorCode implements BaseCode {

    // 400 BAD_REQUEST
    CANNOT_REQUEST_SELF(HttpStatus.BAD_REQUEST, "FRIEND400_1", "자기 자신에게는 친구 신청을 할 수 없습니다."),
    ALREADY_PROCESSING_OR_FRIEND(HttpStatus.BAD_REQUEST, "FRIEND400_2", "이미 친구 신청을 했거나 친구 상태인 회원입니다."),
    ALREADY_PROCESSED_REQUEST(HttpStatus.BAD_REQUEST, "FRIEND400_3", "이미 처리된 친구 신청입니다."),
    NOT_FRIEND(HttpStatus.BAD_REQUEST, "FRIEND400_4", "친구 관계가 아닙니다."),

    // 403 FORBIDDEN
    FRIEND_PAGE_FORBIDDEN(HttpStatus.FORBIDDEN, "FRIEND403_1", "친구만 해당 프로필/페이지를 조회할 수 있습니다."),
    FRIEND_REQUEST_FORBIDDEN(HttpStatus.FORBIDDEN, "FRIEND403_2", "해당 친구 신청을 처리할 권한이 없습니다."),

    // 404 NOT_FOUND
    FRIEND_NOT_FOUND(HttpStatus.NOT_FOUND, "FRIEND404_1", "존재하지 않는 회원입니다."),
    FRIEND_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "FRIEND404_2", "존재하지 않는 친구 신청입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}