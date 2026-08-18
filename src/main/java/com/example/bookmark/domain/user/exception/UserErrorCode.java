package com.example.bookmark.domain.user.exception;

import com.example.bookmark.common.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseCode {

    // 400
    INVALID_INPUT_FORMAT(HttpStatus.BAD_REQUEST, "USER4001", "입력값 형식이 올바르지 않습니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "USER4002", "비밀번호가 일치하지 않습니다."),
    PASSWORD_CONFIRM_MISMATCH(HttpStatus.BAD_REQUEST, "USER4003", "비밀번호와 비밀번호 확인이 일치하지 않습니다."),

    // 401
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "USER4011", "인증이 필요합니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "USER4012", "아이디 또는 비밀번호가 올바르지 않습니다."),

    // 403
    NOT_OWN_ACCOUNT(HttpStatus.FORBIDDEN, "USER4031", "본인의 계정만 접근할 수 있습니다."),

    // 404
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER4041", "존재하지 않는 사용자입니다."),

    // 409
    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "USER4091", "이미 사용 중인 아이디입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "USER4092", "이미 사용 중인 이메일입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}