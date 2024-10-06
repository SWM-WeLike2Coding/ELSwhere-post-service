package com.wl2c.elswherepostservice.domain.post.model.entity;

import com.wl2c.elswherepostservice.global.base.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private String imageId;

    private String contentType;

    private String imageName;

    @Builder
    private PostImage(String imageId, String contentType, String imageName) {
        this.imageId = imageId;
        this.contentType = contentType;
        this.imageName = imageName;
    }

    public void changePost(Post post) {
        if (this.post != null) {
            this.post.getImages().remove(this);
        }

        this.post = post;
        this.post.getImages().add(this);
    }
}