package com.wl2c.elswherepostservice.mock;

import com.wl2c.elswherepostservice.domain.post.model.PostStatus;
import com.wl2c.elswherepostservice.domain.post.model.entity.Post;
import com.wl2c.elswherepostservice.domain.post.model.entity.posttype.Notice;
import com.wl2c.elswherepostservice.global.base.BaseEntity;
import com.wl2c.elswherepostservice.util.EntityUtil;
import com.wl2c.elswherepostservice.util.FieldReflector;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NoticeMock {

    public static List<Notice> createList(String prefix, Long userId, int size) {
        return createList(prefix, userId, size, true);
    }

    public static List<Notice> createList(String prefix, Long userId, int size, boolean enabled) {
        List<Notice> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Notice notice = Notice.builder()
                    .userId(userId)
                    .title(prefix + i)
                    .body(Integer.toString(i))
                    .build();
            if (!enabled) {
                FieldReflector.inject(Post.class, notice, "status", PostStatus.DELETED);
            }
            FieldReflector.inject(BaseEntity.class, notice, "createdAt", LocalDateTime.of(2024, 3, 3, 3, 3));
            result.add(notice);
        }

        return result;
    }

    public static Notice create(Long userId) {
        return create(userId, null, true);
    }

    public static Notice create(Long userId, Long noticeId, boolean enabled) {
        Notice notice = Notice.builder()
                .userId(userId)
                .title("")
                .body("")
                .build();
        if (noticeId != null) {
            EntityUtil.injectId(Post.class, notice, noticeId);
        }
        if (!enabled) {
            FieldReflector.inject(Post.class, notice, "status", PostStatus.DELETED);
        }
        return notice;
    }

}
