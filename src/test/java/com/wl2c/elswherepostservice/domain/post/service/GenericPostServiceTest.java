package com.wl2c.elswherepostservice.domain.post.service;

import com.wl2c.elswherepostservice.domain.client.user.dto.response.ResponseUserNicknameDto;
import com.wl2c.elswherepostservice.domain.post.exception.PostNotFoundException;
import com.wl2c.elswherepostservice.domain.post.model.dto.list.SummarizedGenericPostDto;
import com.wl2c.elswherepostservice.domain.post.model.dto.request.RequestCreateNoticeDto;
import com.wl2c.elswherepostservice.domain.post.model.dto.request.RequestUpdateNoticeDto;
import com.wl2c.elswherepostservice.domain.post.model.dto.response.ResponseSingleGenericPostDto;
import com.wl2c.elswherepostservice.domain.post.model.entity.posttype.Notice;
import com.wl2c.elswherepostservice.domain.post.repository.GenericPostRepository;
import com.wl2c.elswherepostservice.global.auth.role.UserRole;
import com.wl2c.elswherepostservice.infra.s3.service.AWSObjectStorageService;
import com.wl2c.elswherepostservice.infra.s3.service.FileUploadService;
import com.wl2c.elswherepostservice.infra.s3.service.ImageUploadService;
import com.wl2c.elswherepostservice.mock.MultipartFileMock;
import com.wl2c.elswherepostservice.mock.NoticeMock;
import com.wl2c.elswherepostservice.util.DummyPage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class GenericPostServiceTest {

    @Mock
    private CircuitBreakerFactory circuitBreakerFactory;

    @Mock
    private GenericPostRepository<Notice> noticeRepository;

    @Mock
    private AWSObjectStorageService s3service;

    @Mock
    private ImageUploadService imageUploadService;

    @Mock
    private FileUploadService fileUploadService;

    @InjectMocks
    private GenericPostService<Notice> noticeService;

    private List<Notice> noticeList = null;

    @Test
    @DisplayName("list가 잘 동작하는지 확인")
    public void list() {
        // given
        noticeList = NoticeMock.createList("test", 1L, 3, true);

        Page<Notice> noticePage = new DummyPage<>(noticeList, 20);
        when(noticeRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(noticePage);

        CircuitBreaker circuitBreakerMock = Mockito.mock(CircuitBreaker.class);
        when(circuitBreakerFactory.create(any(String.class))).thenReturn(circuitBreakerMock);
        when(circuitBreakerMock.run(any(), any())).thenReturn(new ResponseUserNicknameDto("testUser"));

        // when
        Page<SummarizedGenericPostDto> pages = noticeService.list(noticeRepository, null, Pageable.unpaged(), 100);

        // then
        assertThat(pages.getTotalElements()).isEqualTo(noticeList.size());
        for (int i = 0; i < pages.getTotalElements(); i++) {
            SummarizedGenericPostDto dto = pages.getContent().get(i);
            Notice notice = noticeList.get(i);
            assertThat(dto.getId()).isEqualTo(notice.getId());
            assertThat(dto.getTitle()).isEqualTo(notice.getTitle());
            assertThat(dto.getBody()).isEqualTo(notice.getBody());
            assertThat(dto.getAuthor()).isEqualTo("testUser");
        }
    }

    @Test
    @DisplayName("새롭게 잘 생성되는지 확인")
    public void create() {
        // given
        Notice notice = NoticeMock.create(1L, 1L, true);

        List<MultipartFile> images = MultipartFileMock.createList(10);
        List<MultipartFile> files = MultipartFileMock.createList(10);
        RequestCreateNoticeDto dto = new RequestCreateNoticeDto("title", "body", images, files);

        when(noticeRepository.save(any())).thenReturn(notice);
        when(imageUploadService.uploadedImages(any())).thenReturn(new ArrayList<>());

        // when
        Long result = noticeService.create(noticeRepository, 1L, dto);

        // then
        assertThat(result).isEqualTo(notice.getId());

        verify(imageUploadService).uploadedImages(argThat(list -> {
            for (int i = 0; i < images.size(); i++) {
                assertThat(list.get(i).getOriginalImageName()).isEqualTo(images.get(i).getOriginalFilename());
            }
            return true;
        }));

        verify(noticeRepository).save(argThat(entity -> {
            assertThat(entity.getUserId()).isEqualTo(1L);
            return true;
        }));
    }

    @Test
    @DisplayName("단건 조회가 잘 동작하는지 확인")
    public void findOne() {
        // given
        Notice notice = NoticeMock.create(1L, 1L, true);

        CircuitBreaker circuitBreakerMock = Mockito.mock(CircuitBreaker.class);
        when(noticeRepository.findWithBlindedById(any())).thenReturn(Optional.of(notice));
        when(circuitBreakerFactory.create(any(String.class))).thenReturn(circuitBreakerMock);
        when(circuitBreakerMock.run(any(), any())).thenReturn(new ResponseUserNicknameDto("testUser"));

        // when
        ResponseSingleGenericPostDto dto = noticeService.findOne(noticeRepository, notice.getId(), notice.getUserId(), UserRole.ADMIN.getName());

        // then
        verify(noticeRepository).findWithBlindedById(argThat(id -> {
            assertThat(id).isEqualTo(notice.getId());
            return true;
        }));

        assertThat(dto.getTitle()).isEqualTo("");
        assertThat(dto.getBody()).isEqualTo("");
        assertThat(dto.getAuthor()).isEqualTo("testUser");
        assertThat(dto.isMine()).isEqualTo(true);
    }

    @Test
    @DisplayName("없는 게시글 단건 조회시 오류가 발생하는지 확인")
    public void findOneWithException() {
        // given
        when(noticeRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(PostNotFoundException.class, () ->
                noticeService.findOne(noticeRepository, 0L, 4L, UserRole.USER.getName()));

    }

    @Test
    @DisplayName("update가 잘 동작하는지 확인 - ADMIN")
    public void updatesForAdmin() {
        // given
        Notice notice = NoticeMock.create(1L, 1L, true);

        when(noticeRepository.save(any())).thenReturn(notice);
        when(noticeRepository.findWithBlindedById(any())).thenReturn(Optional.of(notice));

        // when
        noticeService.update(noticeRepository,
                                notice.getId(),
                                notice.getUserId(),
                                UserRole.ADMIN.getName(),
                                new RequestUpdateNoticeDto("update-title", "update-body"));

        // then
        verify(noticeRepository).save(argThat(entity -> {
            assertThat(notice.getTitle()).isEqualTo("update-title");
            assertThat(notice.getBody()).isEqualTo("update-body");
            return true;
        }));
    }

    @Test
    @DisplayName("update가 잘 동작하는지 확인 - USER")
    public void updatesForUser() {
        // given
        Notice notice = NoticeMock.create(1L, 1L, true);

        when(noticeRepository.save(any())).thenReturn(notice);
        when(noticeRepository.findById(any())).thenReturn(Optional.of(notice));

        // when
        noticeService.update(noticeRepository,
                notice.getId(),
                notice.getUserId(),
                UserRole.USER.getName(),
                new RequestUpdateNoticeDto("update-title", "update-body"));

        // then
        verify(noticeRepository).save(argThat(entity -> {
            assertThat(notice.getTitle()).isEqualTo("update-title");
            assertThat(notice.getBody()).isEqualTo("update-body");
            return true;
        }));
    }
}