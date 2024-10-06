package com.wl2c.elswherepostservice.domain.post.model.dto.response;

import com.wl2c.elswherepostservice.domain.post.model.dto.PostFileDto;
import com.wl2c.elswherepostservice.domain.post.model.dto.PostImageDto;
import com.wl2c.elswherepostservice.domain.post.model.entity.Post;
import com.wl2c.elswherepostservice.infra.s3.service.AWSObjectStorageService;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ResponseSingleGenericPostDto {

    @Schema(description = "게시글 아이디", example = "1")
    private final Long id;

    @Schema(description = "게시글 제목", example = "제목")
    private final String title;

    @Schema(description = "게시글 본문", example = "본문")
    private final String body;

    @Schema(description = "작성자", example = "작성자")
    private final String author;

    @Schema(description = "생성 시각")
    private final LocalDateTime createdAt;

    @Schema(description = "이미지 목록")
    private final List<PostImageDto> images;

    @Schema(description = "첨부파일 목록")
    private final List<PostFileDto> files;

    @Schema(description = "내가 쓴 게시물인지?", example = "true")
    private final boolean isMine;

    public ResponseSingleGenericPostDto(AWSObjectStorageService s3service, boolean isMine, Post post, String nickname) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.body = post.getBody();
        this.author = nickname;
        this.createdAt = post.getCreatedAt();
        this.images = PostImageDto.listOf(s3service, post.getImages());
        this.files = PostFileDto.listOf(s3service, post.getFiles());
        this.isMine = isMine;
    }

}
