package com.wl2c.elswherepostservice.infra.s3.exception;

import com.wl2c.elswherepostservice.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class InvalidFileContentTypeException extends LocalizedMessageException {

    public InvalidFileContentTypeException(Throwable e) {
        super(e, HttpStatus.BAD_REQUEST, "invalid.file-content-type");
    }
}
