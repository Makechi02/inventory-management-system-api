package com.makechi.invizio.controller.user.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.makechi.invizio.dto.user.UserDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserAuthResponse {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    private String message;

    private UserDto user;
}