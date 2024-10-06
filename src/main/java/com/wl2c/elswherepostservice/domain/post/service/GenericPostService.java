package com.wl2c.elswherepostservice.domain.post.service;

import com.wl2c.elswherepostservice.domain.client.user.api.UserServiceClient;
import com.wl2c.elswherepostservice.domain.client.user.dto.response.ResponseUserNicknameDto;
import com.wl2c.elswherepostservice.domain.post.exception.PostNotFoundException;
import com.wl2c.elswherepostservice.domain.post.model.dto.list.SummarizedGenericPostDto;
import com.wl2c.elswherepostservice.domain.post.model.dto.request.RequestCreateGenericPostDto;
import com.wl2c.elswherepostservice.domain.post.model.dto.request.RequestUpdateGenericPostDto;
import com.wl2c.elswherepostservice.domain.post.model.dto.response.ResponseSingleGenericPostDto;
import com.wl2c.elswherepostservice.domain.post.model.entity.Post;
import com.wl2c.elswherepostservice.domain.post.model.entity.PostImage;
import com.wl2c.elswherepostservice.domain.post.repository.GenericPostRepository;
import com.wl2c.elswherepostservice.domain.post.repository.spec.PostSpec;
import com.wl2c.elswherepostservice.global.auth.role.UserRole;
import com.wl2c.elswherepostservice.global.error.exception.NotGrantedException;
import com.wl2c.elswherepostservice.infra.s3.dto.ImageRequest;
import com.wl2c.elswherepostservice.infra.s3.dto.UploadedImage;
import com.wl2c.elswherepostservice.infra.s3.service.AWSObjectStorageService;
import com.wl2c.elswherepostservice.infra.s3.service.FileUploadService;
import com.wl2c.elswherepostservice.infra.s3.service.ImageUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenericPostService<E extends Post> {

    private final CircuitBreakerFactory circuitBreakerFactory;

    private final UserServiceClient userServiceClient;

    protected final AWSObjectStorageService s3service;
    protected final ImageUploadService imageUploadService;
    protected final FileUploadService fileUploadService;

    /**
     * 게시글 전체 조회
     *
     * @param spec       검색어
     * @param bodySize   본문 길이
     */
    @Transactional(readOnly = true)
    public Page<SummarizedGenericPostDto> list(GenericPostRepository<E> repository,
                                               Specification<E> spec,
                                               Pageable pageable,
                                               int bodySize) {
        Page<E> result = list(repository, spec, pageable);

        List<SummarizedGenericPostDto> filteredList = result.stream()
                .map(post -> makeListDto(bodySize, post))
                .filter(Objects::nonNull)  // null이 아닌 값만 포함
                .collect(Collectors.toList());

        return new PageImpl<>(filteredList, pageable, result.getTotalElements());
    }

    private Page<E> list(GenericPostRepository<E> repository, Specification<E> spec, Pageable pageable) {
        if (spec == null) {
            spec = Specification.where(null);
        }

        spec = spec.and(PostSpec.withActive());

        return repository.findAll(spec, pageable);
    }

    public SummarizedGenericPostDto makeListDto(int bodySize, E post) {

        // feignClient로 유저 닉네임 받아오기
        log.info("Before call the user microservice");
        CircuitBreaker circuitBreakerAboutUser = circuitBreakerFactory.create("findUserNicknameCircuitBreaker");
        ResponseUserNicknameDto responseUserNicknameDto =
                circuitBreakerAboutUser.run(() -> userServiceClient.getUserNickname(UserRole.ADMIN.getName(), post.getUserId()),
                        throwable -> null
                );
        log.info("after called the user microservice");

        // 존재하지 않는 유저(탈퇴 등)의 글의 대해서는 null로 처리
        if (responseUserNicknameDto == null)
            return null;

        return new SummarizedGenericPostDto(s3service, bodySize, post, responseUserNicknameDto.getNickname());
    }

    /**
     * 게시글 등록
     */
    @Transactional
    public Long create(GenericPostRepository<E> repository, Long userId, RequestCreateGenericPostDto<E> dto) {
        E post = dto.toEntity(userId);

        attachImages(dto.getImages(), post);

        E savedPost = repository.save(post);
        return savedPost.getId();
    }

    private void attachImages(List<MultipartFile> dtoImages, E post) {
        List<UploadedImage> images = imageUploadService.uploadedImages(
                ImageRequest.ofList(dtoImages)
        );

        List<PostImage> postImages = new ArrayList<>();
        for (UploadedImage image : images) {
            PostImage.PostImageBuilder builder = PostImage.builder()
                    .imageName(image.getOriginalImageName())
                    .contentType(image.getMimeType().toString())
                    .imageId(image.getFileId());

            postImages.add(builder.build());
        }
        for (PostImage file : postImages) {
            file.changePost(post);
        }
    }

    /**
     * 게시글 단건 조회
     */
    @Transactional
    public ResponseSingleGenericPostDto findOne(GenericPostRepository<E> repository, Long postId, @Nullable Long userId,
                                                String userRole) {
        E post = findPost(repository, postId, userRole);
        return makePostDto(userId, post);
    }

    private ResponseSingleGenericPostDto makePostDto(@Nullable Long userId, E post) {
        boolean isMine = false;

        if (userId != null) {
            isMine = post.getUserId().equals(userId);
        }

        // feignClient로 유저 닉네임 받아오기
        log.info("Before call the user microservice");
        CircuitBreaker circuitBreakerAboutUser = circuitBreakerFactory.create("findUserNicknameCircuitBreaker");
        ResponseUserNicknameDto responseUserNicknameDto =
                circuitBreakerAboutUser.run(() -> userServiceClient.getUserNickname(UserRole.ADMIN.getName(), post.getUserId()),
                        throwable -> null
                );
        log.info("after called the user microservice");

        // 존재하지 않는 유저(탈퇴 등)의 글의 대해서는 null로 처리
        if (responseUserNicknameDto == null)
            return null;

        return new ResponseSingleGenericPostDto(s3service, isMine, post, responseUserNicknameDto.getNickname());
    }

    private E findPost(GenericPostRepository<E> repository, Long postId, String userRole) {
        Optional<E> post;
        if (UserRole.of(userRole).equals(UserRole.ADMIN)) {
            post = repository.findWithBlindedById(postId);
        } else {
            post = repository.findById(postId);
        }
        return post.orElseThrow(PostNotFoundException::new);
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public void update(GenericPostRepository<E> repository, Long postId, Long userId, String userRole, RequestUpdateGenericPostDto<E> dto) {
        E post = findPost(repository, postId, userRole);
        if (!post.getUserId().equals(userId)) {
            throw new NotGrantedException();
        }
        post.update(dto.getTitle(), dto.getBody());

        repository.save(post);
    }

    /**
     * 게시글 삭제. 실제 DB에서 삭제처리되지 않고 표시만 해둔다.
     *
     * @param repository 게시글 repository
     * @param postId     게시글 id
     * @param userId     삭제하는 사용자 id
     * @param userRole   사용자 역할
     */
    @Transactional
    public void delete(GenericPostRepository<E> repository, Long postId, Long userId, String userRole) {
        E post = repository.findById(postId).orElseThrow(PostNotFoundException::new);
        if (UserRole.of(userRole).equals(UserRole.ADMIN)) {
            post.markAsDeleted(true);
        } else if (post.getUserId().equals(userId)) {
            post.markAsDeleted(false);
        } else {
            throw new NotGrantedException();
        }
    }

}
