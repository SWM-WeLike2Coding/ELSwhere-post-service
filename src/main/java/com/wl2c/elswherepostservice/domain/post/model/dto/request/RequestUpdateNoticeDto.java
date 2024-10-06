package com.wl2c.elswherepostservice.domain.post.model.dto.request;

import com.wl2c.elswherepostservice.domain.post.model.entity.posttype.Notice;
import lombok.Getter;

@Getter
public class RequestUpdateNoticeDto extends RequestUpdateGenericPostDto<Notice> {
    public RequestUpdateNoticeDto(String title, String body) {
        super(title, body);
    }

    @Override
    public Notice toEntity(Long userId) {
        return Notice.builder()
                .userId(userId)
                .title(getTitle())
                .body(getBody())
                .build();
    }
}
