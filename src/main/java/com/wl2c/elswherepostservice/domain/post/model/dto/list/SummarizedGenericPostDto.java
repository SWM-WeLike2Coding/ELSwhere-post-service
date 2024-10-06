package com.wl2c.elswherepostservice.domain.post.model.dto.list;

import com.wl2c.elswherepostservice.domain.post.model.dto.PostFileDto;
import com.wl2c.elswherepostservice.domain.post.model.dto.PostImageDto;
import com.wl2c.elswherepostservice.domain.post.model.entity.Post;
import com.wl2c.elswherepostservice.infra.s3.service.AWSObjectStorageService;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class SummarizedGenericPostDto {

    @Schema(description = "게시글 아이디", example = "1")
    private final Long id;

    @Schema(description = "제목", example = "게시글 제목")
    private final String title;

    @Schema(description = "작성자", example = "익명")
    private final String author;

    @Schema(description = "본문", example = "게시글 본문")
    private final String body;

    @Schema(description = "이미지 목록")
    private final List<PostImageDto> images;

    @Schema(description = "첨부파일 목록")
    private final List<PostFileDto> files;

    public SummarizedGenericPostDto(AWSObjectStorageService s3service, int bodySize, Post post, String nickname) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.author = nickname;
        this.body = slice(post.getBody(), bodySize);
        this.images = PostImageDto.listOf(s3service, post.getImages());
        this.files = PostFileDto.listOf(s3service, post.getFiles());
    }

    private static String slice(String text, int maxLen) {
        if (text == null) {
            return null;
        }
        return text.substring(0, Math.min(text.length(), maxLen));
    }
}
