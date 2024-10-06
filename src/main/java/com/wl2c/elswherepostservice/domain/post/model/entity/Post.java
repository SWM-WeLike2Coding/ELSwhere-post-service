package com.wl2c.elswherepostservice.domain.post.model.entity;

import com.wl2c.elswherepostservice.domain.post.model.PostStatus;
import com.wl2c.elswherepostservice.global.base.BaseEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.InheritanceType.SINGLE_TABLE;
import static lombok.AccessLevel.PROTECTED;

/**
 * Post BaseEntity.
 * 이 클래스를 상속받으면 공지사항, 자유게시판 등과 같은 새로운 타입을 만들 수 있다.
 * 상속받고 필요한대로 확장해서 사용하면 된다.
 */
@Entity
@Getter
@DynamicUpdate
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@NoArgsConstructor(access = PROTECTED)
public abstract class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @NotNull
    private Long userId;

    private String title;

    @Lob
    private String body;

    @Enumerated(STRING)
    private PostStatus status;

    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<PostFile> files = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<PostImage> images = new ArrayList<>();

    protected Post(Long userId, String title, String body) {
        this.userId = userId;
        this.title = title;
        this.body = body;
        this.status = PostStatus.ACTIVE;
    }

    public void update(String title, String body) {
        this.title = title;
        this.body = body;
    }
    public void markAsDeleted(boolean byAdmin) {
        this.status = byAdmin ? PostStatus.DELETED_BY_ADMIN : PostStatus.DELETED;
    }

    public void blind() {
        this.status = PostStatus.BLINDED;
    }

    public void unblind() {
        this.status = PostStatus.ACTIVE;
    }

}