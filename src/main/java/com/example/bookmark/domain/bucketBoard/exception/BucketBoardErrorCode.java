package com.example.bookmark.domain.bucketBoard.exception;

import com.example.bookmark.common.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BucketBoardErrorCode implements BaseCode {

    INVALID_INPUT_FORMAT(HttpStatus.BAD_REQUEST, "BOARD4001", "입력값 형식이 올바르지 않습니다."),
    INVALID_MEMO_POSITION(HttpStatus.BAD_REQUEST, "BOARD4002", "메모 좌표는 0.0 이상 1.0 이하여야 합니다."),
    INVALID_MEMO_STATE(HttpStatus.BAD_REQUEST, "BOARD4003", "유효하지 않은 메모 상태값입니다."),
    MEMO_CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "BOARD4004", "메모 내용이 최대 길이를 초과했습니다."),
    DESIGN_NOT_IN_THEME(HttpStatus.BAD_REQUEST, "BOARD4005", "현재 테마에 속하지 않는 메모지 디자인입니다."),
    CATEGORY_NAME_DUPLICATED(HttpStatus.BAD_REQUEST, "BOARD4006", "이미 존재하는 카테고리 이름입니다."),
    CANNOT_MODIFY_DEFAULT_CATEGORY(HttpStatus.BAD_REQUEST, "BOARD4007", "기본 카테고리는 수정하거나 삭제할 수 없습니다."),
    MEMO_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "BOARD4008", "이미 완료 처리된 메모입니다."),
    MEMO_ALREADY_SCRAPPED(HttpStatus.BAD_REQUEST, "BOARD4009", "이미 콜렉트북에 기록된 메모입니다."),
    BOARD_THEME_ALREADY_SELECTED(HttpStatus.BAD_REQUEST, "BOARD4010", "이미 선택되어 있는 테마입니다."),

    // 403
    NOT_OWN_MEMO(HttpStatus.FORBIDDEN, "BOARD4031", "본인의 메모만 접근할 수 있습니다."),
    NOT_OWN_CATEGORY(HttpStatus.FORBIDDEN, "BOARD4032", "본인의 카테고리만 접근할 수 있습니다."),

    // 404
    MEMO_NOT_FOUND(HttpStatus.NOT_FOUND, "BOARD4041", "존재하지 않는 메모입니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "BOARD4042", "존재하지 않는 카테고리입니다."),
    THEME_NOT_FOUND(HttpStatus.NOT_FOUND, "BOARD4043", "존재하지 않는 보드 테마입니다."),
    MEMO_DESIGN_NOT_FOUND(HttpStatus.NOT_FOUND, "BOARD4044", "존재하지 않는 메모지 디자인입니다."),
    THEME_HAS_NO_DESIGN(HttpStatus.NOT_FOUND, "BOARD4045", "테마에 등록된 메모지 디자인이 없습니다."),
    BUCKET_BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "BOARD4046", "존재하지 않는 버킷보드입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
