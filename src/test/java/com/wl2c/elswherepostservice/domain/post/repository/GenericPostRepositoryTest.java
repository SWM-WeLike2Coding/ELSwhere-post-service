package com.wl2c.elswherepostservice.domain.post.repository;

import com.wl2c.elswherepostservice.domain.post.model.entity.posttype.Notice;
import com.wl2c.elswherepostservice.mock.NoticeMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class GenericPostRepositoryTest {

    @Autowired
    private GenericPostRepository<Notice> noticeRepository;

    @BeforeEach
    public void setup() {
        noticeRepository.deleteAll();
        noticeRepository.saveAll(NoticeMock.createList("test", 1L, 3, true));
    }

    @Test
    @DisplayName("게시글 전체 조회가 잘 되는지?")
    void findAll() {
        // when
        Page<Notice> result = noticeRepository.findAll((root, query, builder) -> null, Pageable.unpaged());

        // then
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    @DisplayName("ACTIVE 상태인 post만 잘 가져오는지?")
    void findById() {
        // given
        noticeRepository.save(NoticeMock.create(1L, 4L, false));
        noticeRepository.save(NoticeMock.create(1L, 5L, true));

        // when
        Optional<Notice> notice1 = noticeRepository.findById(4L);
        Optional<Notice> notice2 = noticeRepository.findById(5L);

        // then
        assertThat(notice1.isEmpty()).isTrue();
        assertThat(notice2.isEmpty()).isFalse();
    }

}
