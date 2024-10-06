package com.wl2c.elswherepostservice.domain.post.model.dto.request;

import com.wl2c.elswherepostservice.domain.post.model.entity.posttype.Notice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class RequestCreateNoticeDto extends RequestCreateGenericPostDto<Notice> {

    public RequestCreateNoticeDto(@NotBlank String title, @NotBlank String body, List<MultipartFile> images, List<MultipartFile> files) {
        super(title, body, images, files);
    }

    public Notice toEntity(Long userId) {
        return Notice.builder()
                .userId(userId)
                .title(getTitle())
                .body(getBody())
                .build();
    }
}
