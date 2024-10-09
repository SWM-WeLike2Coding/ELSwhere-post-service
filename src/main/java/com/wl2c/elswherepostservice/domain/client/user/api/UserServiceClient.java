package com.wl2c.elswherepostservice.domain.client.user.api;

import com.wl2c.elswherepostservice.domain.client.user.dto.response.ResponseUserNicknameDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/v1/user/nickname/{id}")
    ResponseUserNicknameDto getUserNickname(@RequestHeader("requestRole") String requestRole, @PathVariable Long id);

}
