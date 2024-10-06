package com.wl2c.elswherepostservice.infra.s3.exception;

import com.wl2c.elswherepostservice.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class InvalidImageContentTypeException extends LocalizedMessageException {

    public InvalidImageContentTypeException(Throwable e) {
        super(e, HttpStatus.BAD_REQUEST, "invalid.image-content-type");
    }
}
