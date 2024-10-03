package com.wl2c.elswherepostservice.global.error.exception;

import org.springframework.http.HttpStatus;

public class AccessTokenNotFoundException extends LocalizedMessageException {
    public AccessTokenNotFoundException() {
        super(HttpStatus.NOT_FOUND, "notfound.access-token");
    }
}
