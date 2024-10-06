package com.wl2c.elswherepostservice.domain.post.model.dto;

import com.wl2c.elswherepostservice.domain.post.model.entity.PostFile;
import com.wl2c.elswherepostservice.infra.s3.service.AWSObjectStorageService;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class PostFileDto {

    @Schema(description = "파일 아이디", example = "1")
    private final Long id;

    @Schema(description = "파일 url", example = "http://1.2.3.4/1ddee68d-6aftatub-48d0-9cb6-04a8d8fea4ae.png")
    private final String url;

    @Schema(description = "원본파일 이름", example = "my_text.txt")
    private final String originalName;

    @Schema(description = "파일 타입", example = "text/plain")
    private final String mimeType;

    public PostFileDto(AWSObjectStorageService s3service, PostFile file) {
        this.id = file.getId();
        this.url = s3service.getFileUrl(file.getFileId());
        this.originalName = file.getFileName();

        String fileMimeType = file.getContentType();
        this.mimeType = Objects.requireNonNullElse(fileMimeType, MediaType.APPLICATION_OCTET_STREAM_VALUE);
    }

    public static List<PostFileDto> listOf(AWSObjectStorageService s3service, List<PostFile> entities) {
        List<PostFile> result = new ArrayList<>();

        for (PostFile entity : entities) {
            if (entity.getFileId() != null) {
                result.add(entity);
            }
        }
        return result.stream()
                .map(file -> new PostFileDto(s3service, file))
                .collect(Collectors.toList());
    }
}
