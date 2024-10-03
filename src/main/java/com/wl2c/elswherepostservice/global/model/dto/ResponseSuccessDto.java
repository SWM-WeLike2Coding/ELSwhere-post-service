package com.wl2c.elswherepostservice.global.model.dto;

import lombok.Getter;

/**
 * 요청 처리 성공시 딱히 반환할 dto가 없을 때 사용한다. (ok를 반환하는 상황)
 * 반환해야 할 dto가 있다면, 이걸 사용하지말고 그 dto를 그대로 반환하자.
 */
@Getter
public class ResponseSuccessDto {

    private final String message;

    public ResponseSuccessDto() {
        this("ok");
    }

    public ResponseSuccessDto(String message) {
        this.message = message;
    }
}
