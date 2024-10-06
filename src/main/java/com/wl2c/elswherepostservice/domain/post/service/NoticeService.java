package com.wl2c.elswherepostservice.domain.post.service;

import com.wl2c.elswherepostservice.domain.post.exception.PostNotFoundException;
import com.wl2c.elswherepostservice.domain.post.model.dto.list.SummarizedGenericPostDto;
import com.wl2c.elswherepostservice.domain.post.model.dto.request.RequestCreateNoticeDto;
import com.wl2c.elswherepostservice.domain.post.model.dto.request.RequestUpdateNoticeDto;
import com.wl2c.elswherepostservice.domain.post.model.dto.response.ResponseSingleGenericPostDto;
import com.wl2c.elswherepostservice.domain.post.model.entity.PostFile;
import com.wl2c.elswherepostservice.domain.post.model.entity.posttype.Notice;
import com.wl2c.elswherepostservice.domain.post.repository.NoticeRepository;
import com.wl2c.elswherepostservice.domain.post.repository.spec.PostSpec;
import com.wl2c.elswherepostservice.global.auth.role.UserRole;
import com.wl2c.elswherepostservice.global.error.exception.NotGrantedException;
import com.wl2c.elswherepostservice.infra.s3.dto.FileRequest;
import com.wl2c.elswherepostservice.infra.s3.dto.UploadedFile;
import com.wl2c.elswherepostservice.infra.s3.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final GenericPostService<Notice> postService;
    private final FileUploadService fileUploadService;
    private final NoticeRepository repository;

    @Transactional
    public Long create(Long userId, String userRole, RequestCreateNoticeDto dto) {
        if (!UserRole.of(userRole).equals(UserRole.ADMIN)) {
            throw new NotGrantedException();
        }

        Long postId = postService.create(repository, userId, dto);
        Notice notice = repository.findById(postId).orElseThrow(PostNotFoundException::new);

        attachFiles(dto.getFiles(), notice);

        return postId;
    }

    private void attachFiles(List<MultipartFile> dtoFiles, Notice post) {
        List<UploadedFile> files = fileUploadService.uploadedFiles(
                FileRequest.ofList(dtoFiles)
        );

        List<PostFile> postFiles = new ArrayList<>();
        for (UploadedFile file: files) {
            PostFile.PostFileBuilder builder = PostFile.builder()
                    .fileName(file.getOriginalFileName())
                    .contentType(file.getMimeType().toString())
                    .fileId(file.getFileId());

            postFiles.add(builder.build());
        }
        for (PostFile file : postFiles) {
            file.changePost(post);
        }
    }

    public Page<SummarizedGenericPostDto> list(String keyword, Pageable pageable, int bodySize) {
        Specification<Notice> spec = PostSpec.withTitleOrBody(keyword);
        return postService.list(repository, spec, pageable, bodySize);
    }

    public ResponseSingleGenericPostDto findOne(Long id, Long userId, String userRole) {
        return postService.findOne(repository, id, userId, userRole);
    }

    public void update(Long postId, Long userId, String userRole, RequestUpdateNoticeDto dto) {
        postService.update(repository, postId, userId, userRole, dto);
    }

    public void delete(Long postId, Long userId, String userRole) {
        postService.delete(repository, postId, userId, userRole);
    }
}
