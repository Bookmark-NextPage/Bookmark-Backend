package com.example.bookmark.domain.record.dto.response;

import com.example.bookmark.domain.record.entity.Keyword;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "태그 응답 DTO")
public class TagResponse {

    @Schema(description = "태그(키워드) ID", example = "1")
    private Long keywordId;

    @Schema(description = "태그 이름", example = "카페투어")
    private String name;

    public static TagResponse from(Keyword keyword) {
        return TagResponse.builder()
                .keywordId(keyword.getId())
                .name(keyword.getName())
                .build();
    }
}