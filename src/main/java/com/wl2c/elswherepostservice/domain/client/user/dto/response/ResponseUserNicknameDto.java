package com.wl2c.elswherepostservice.domain.client.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseUserNicknameDto {

    @Schema(description = "닉네임", example = "홍길동")
    private String nickname;

}
