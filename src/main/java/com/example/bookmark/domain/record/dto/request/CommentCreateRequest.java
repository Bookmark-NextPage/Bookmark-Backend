package com.example.bookmark.domain.record.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "기록 댓글 작성 요청 DTO")
public class CommentCreateRequest {

    @NotBlank(message = "댓글 내용은 필수 입력값입니다.")
    @Size(max = 100, message = "댓글은 최대 100자까지 작성할 수 있습니다.")
    @Schema(description = "댓글 내용", example = "혼자 여행 진짜 용기 있다!!")
    private String content;
}