package com.wl2c.elswherepostservice.infra.s3.exception;

import com.wl2c.elswherepostservice.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class InvalidAccessObjectStorageException extends LocalizedMessageException {
    public InvalidAccessObjectStorageException(Throwable e) {
        super(e, HttpStatus.BAD_REQUEST, "invalid.access-s3");
    }
}
