package com.wl2c.elswherepostservice.global.error.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends LocalizedMessageException {
    public InvalidTokenException() {
        super(HttpStatus.UNAUTHORIZED, "invalid.token");
    }
}
