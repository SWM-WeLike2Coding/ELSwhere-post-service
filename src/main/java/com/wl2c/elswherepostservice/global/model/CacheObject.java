package com.wl2c.elswherepostservice.global.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Getter
@RequiredArgsConstructor
public class CacheObject<T> {
    private final Instant expiresAt;
    private final T value;
}