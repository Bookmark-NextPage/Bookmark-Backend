package com.example.bookmark.domain.record.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "태그 생성 요청 DTO")
public class TagCreateRequest {

    @NotBlank(message = "태그 이름은 필수 항목입니다.")
    @Size(max = 10, message = "태그 이름은 최대 10자까지 입력 가능합니다.")
    @Schema(description = "생성할 태그 이름", example = "카페투어")
    private String name;
}