package com.wl2c.elswherepostservice.domain.post.repository;

import com.wl2c.elswherepostservice.domain.post.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GenericPostRepository<T extends Post> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {

    /**
     * ACTIVE상태인 post만 가져옵니다.
     */
    @Override
    @Query("select p from Post p " +
            "where p.id=:id and p.status='ACTIVE' ")
    Optional<T> findById(@Param("id") Long id);

    /**
     * ACTIVE 상태와 BLINDED 상태인 post 모두 가져옵니다.
     */
    @Query("select p from Post p " +
            "where p.id = :id and (p.status='BLINDED' or p.status='ACTIVE')")
    Optional<T> findWithBlindedById(@Param("id") Long id);

    @Override
    Page<T> findAll(Specification<T> spec, Pageable pageable);
}
