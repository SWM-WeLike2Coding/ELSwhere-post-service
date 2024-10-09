package com.wl2c.elswherepostservice.domain.post.exception;

import com.wl2c.elswherepostservice.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class PostNotFoundException extends LocalizedMessageException {

    public PostNotFoundException() {
        super(HttpStatus.NOT_FOUND, "notfound.post");
    }
}
