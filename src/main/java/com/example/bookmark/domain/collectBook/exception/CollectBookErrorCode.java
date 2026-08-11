package com.example.bookmark.domain.collectBook.exception;

import com.example.bookmark.common.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CollectBookErrorCode implements BaseCode {

    COLLECT_BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "COLLECT404_1", "존재하지 않는 콜렉트북입니다."),
    COLLECT_BOOK_FORBIDDEN(HttpStatus.FORBIDDEN, "COLLECT403_1", "해당 콜렉트북에 대한 권한이 없습니다."),
    PRIVATE_COLLECT_BOOK(HttpStatus.FORBIDDEN, "COLLECT403_2", "비공개 콜렉트북입니다."),
    FRIENDS_ONLY_COLLECT_BOOK(HttpStatus.FORBIDDEN, "COLLECT403_3", "친구에게만 공개된 콜렉트북입니다."),
    SYSTEM_COLLECT_BOOK_DELETE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "COLLECT400_1", "시스템에서 자동 생성된 콜렉트북은 삭제할 수 없습니다."),
    CHAPTER_COUNT_INVALID(HttpStatus.BAD_REQUEST, "COLLECT400_2", "챕터 개수 설정이 올바르지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}