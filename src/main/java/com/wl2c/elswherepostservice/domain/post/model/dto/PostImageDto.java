package com.wl2c.elswherepostservice.domain.post.model.dto;

import com.wl2c.elswherepostservice.domain.post.model.entity.PostImage;
import com.wl2c.elswherepostservice.infra.s3.service.AWSObjectStorageService;
import lombok.Getter;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class PostImageDto {

    private final Long id;

    private final String url;

    private final String originalName;

    private final String mimeType;

    public PostImageDto(AWSObjectStorageService s3service, PostImage image) {
        this.id = image.getId();
        this.url = s3service.getImageUrl(image.getImageId());
        this.originalName = image.getImageName();

        String fileMimeType = image.getContentType();
        this.mimeType = Objects.requireNonNullElse(fileMimeType, MediaType.APPLICATION_OCTET_STREAM_VALUE);
    }

    public static List<PostImageDto> listOf(AWSObjectStorageService s3service, List<PostImage> entities) {
        List<PostImage> result = new ArrayList<>();

        for (PostImage entity : entities) {
            if (entity.getImageId() != null) {
                result.add(entity);
            }
        }
        return result.stream()
                .map(image -> new PostImageDto(s3service, image))
                .collect(Collectors.toList());
    }
}
