package com.wl2c.elswherepostservice.domain.post.model.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public abstract class RequestCreateGenericPostDto<T> {

    @NotBlank
    @Schema(description = "제목", example = "제목")
    private final String title;

    @NotBlank
    @Schema(description = "본문", example = "내용")
    private final String body;

    @Schema(description = "이미지 파일 목록")
    private final List<MultipartFile> images;

    @Schema(description = "첨부파일 목록")
    private final List<MultipartFile> files;

    public RequestCreateGenericPostDto(String title, String body, List<MultipartFile> images, List<MultipartFile> files) {
        this.title = title;
        this.body = body;
        this.images = Objects.requireNonNullElseGet(images, ArrayList::new);
        this.files = Objects.requireNonNullElseGet(files, ArrayList::new);
    }

    public abstract T toEntity(Long userId);
}
