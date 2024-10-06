package com.wl2c.elswherepostservice.domain.post.model.entity.posttype;

import com.wl2c.elswherepostservice.domain.post.model.entity.Post;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import static lombok.AccessLevel.PROTECTED;

/**
 * 공지사항 Entity
 */
@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Notice extends Post {

    @Builder
    private Notice(@NonNull Long userId,
                   @NonNull String title,
                   @NonNull String body) {
        super(userId, title, body);
    }


}
