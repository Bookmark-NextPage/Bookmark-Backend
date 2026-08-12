package com.example.bookmark.domain.record.exception;

import com.example.bookmark.common.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RecordErrorCode implements BaseCode {

    // Record 관련
    RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "RECORD404_1", "존재하지 않는 기록입니다."),
    RECORD_FORBIDDEN(HttpStatus.FORBIDDEN, "RECORD403_1", "해당 기록에 대한 접근/수정 권한이 없습니다."),
    INVALID_RECORD_CHAPTER(HttpStatus.BAD_REQUEST, "RECORD400_1", "해당 콜렉트북에 속한 기록이 아닙니다."),
    LIKE_ALREADY_EXISTS(HttpStatus.CONFLICT, "RECORD409_1", "이미 좋아요를 누른 기록입니다."),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "RECORD404_2", "좋아요 기록을 찾을 수 없습니다."),

    // 연관 엔티티 관련
    CHAPTER_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAPTER404_1", "존재하지 않는 챕터입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404_1", "존재하지 않는 회원입니다."),
    MEMO_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMO404_1", "존재하지 않는 버킷보드 메모입니다."),
    FRIENDSHIP_REQUIRED(HttpStatus.FORBIDDEN, "FRIEND403_1", "친구 관계인 유저만 댓글 및 좋아요를 남길 수 있습니다."),
    KEYWORD_NOT_FOUND(HttpStatus.NOT_FOUND, "KEYWORD404_1", "존재하지 않는 키워드(태그)가 포함되어 있습니다."),


    // 태그(감성 키워드) 관련
    TAG_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "TAG400_1", "태그는 최대 10개까지만 생성할 수 있습니다."),
    DUPLICATE_TAG_NAME(HttpStatus.CONFLICT, "TAG409_1", "이미 존재하는 태그 이름입니다."),

    // 이미지 업로드 관련
    IMAGE_COUNT_EXCEEDED(HttpStatus.BAD_REQUEST, "IMAGE400_1", "이미지는 한 번에 최대 5장까지만 업로드할 수 있습니다."),
    INVALID_IMAGE_EXTENSION(HttpStatus.BAD_REQUEST, "IMAGE400_2", "지원하지 않는 이미지 확장자입니다. (png, jpg, jpeg만 가능)"),

    // AI 이미지 생성 관련
    AI_RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "AI429_1", "AI 이미지 생성은 하루에 최대 10회까지만 이용 가능합니다."),
    AI_IMAGE_GENERATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AI500_1", "AI 스크랩북 이미지 생성 도중 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}