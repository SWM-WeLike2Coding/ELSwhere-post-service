package com.wl2c.elswherepostservice.domain.post.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RequestTitleAndBodyDto {

    @NotBlank
    @Schema(description = "제목", example = "제목")
    private final String title;

    @NotBlank
    @Schema(description = "본문", example = "내용")
    private final String body;

    public RequestTitleAndBodyDto(String title, String body) {
        this.title = title;
        this.body = body;
    }
}
