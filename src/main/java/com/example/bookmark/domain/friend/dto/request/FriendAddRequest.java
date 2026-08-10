package com.example.bookmark.domain.friend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "친구 추가 요청 DTO")
public record FriendAddRequest(
        @Schema(description = "추가할 친구의 userId", example = "2")
        @NotNull(message = "친구 userId는 필수입니다.")
        Long friendUserId
) {}