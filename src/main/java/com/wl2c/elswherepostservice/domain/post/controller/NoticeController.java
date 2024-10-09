package com.wl2c.elswherepostservice.domain.post.controller;

import com.wl2c.elswherepostservice.domain.post.model.dto.list.SummarizedGenericPostDto;
import com.wl2c.elswherepostservice.domain.post.model.dto.request.RequestCreateNoticeDto;
import com.wl2c.elswherepostservice.domain.post.model.dto.request.RequestTitleAndBodyDto;
import com.wl2c.elswherepostservice.domain.post.model.dto.request.RequestUpdateNoticeDto;
import com.wl2c.elswherepostservice.global.model.dto.ResponsePage;
import com.wl2c.elswherepostservice.domain.post.model.dto.response.ResponseSingleGenericPostDto;
import com.wl2c.elswherepostservice.domain.post.service.NoticeService;
import com.wl2c.elswherepostservice.global.model.dto.ResponseIdDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static java.lang.Long.parseLong;

@Tag(name = "공지", description = "공지사항 게시글 관련 api")
@RestController
@RequestMapping("/v1/post/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * 공지사항 목록 조회
     *
     * @param keyword  제목이나 내용에 포함된 검색어. 지정하지않으면 모든 게시글 조회.
     * @param bodySize 게시글 본문 길이. (글자 단위) 지정하지 않으면 50 글자.
     * @return 페이징된 공지 목록
     */
    @GetMapping
    public ResponsePage<SummarizedGenericPostDto> list(@RequestParam(required = false) String keyword,
                                                       @RequestParam(defaultValue = "50") int bodySize,
                                                       @ParameterObject Pageable pageable) {
        Page<SummarizedGenericPostDto> list = noticeService.list(keyword, pageable, bodySize);
        return new ResponsePage<>(list);
    }

    /**
     * 공지사항 글 등록
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseIdDto create(HttpServletRequest request,
                                @Valid @RequestPart RequestTitleAndBodyDto requestTitleAndBodyDto,
                                @Valid @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                @Valid @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        RequestCreateNoticeDto requestCreateNoticeDto = new RequestCreateNoticeDto(requestTitleAndBodyDto.getTitle(), requestTitleAndBodyDto.getBody(), images, files);
        Long postId = noticeService.create(parseLong(request.getHeader("requestId")), request.getHeader("requestRole"), requestCreateNoticeDto);

        return new ResponseIdDto(postId);
    }

    /**
     * 공지사항 글 단건 조회
     *
     * @param id     조회할 게시글 id
     */
    @GetMapping("/{id}")
    public ResponseSingleGenericPostDto findOne(HttpServletRequest request,
                                                @PathVariable Long id) {
        return noticeService.findOne(id,
                parseLong(request.getHeader("requestId")),
                request.getHeader("requestRole")
        );
    }

    /**
     * 공지사항 글 수정
     *
     * @param id     수정할 게시글 id
     */
    @PatchMapping("/{id}")
    public void update(HttpServletRequest request,
                       @PathVariable Long id,
                       @RequestBody RequestUpdateNoticeDto dto) {
        noticeService.update(id,
                parseLong(request.getHeader("requestId")),
                request.getHeader("requestRole"), dto
        );
    }

    /**
     * 공지사항 글 삭제
     *
     * @param id    삭제할 게시글 id
     */
    @DeleteMapping("/{id}")
    public void delete(HttpServletRequest request, @PathVariable Long id) {
        noticeService.delete(id,
                parseLong(request.getHeader("requestId")),
                request.getHeader("requestRole")
        );
    }

}
