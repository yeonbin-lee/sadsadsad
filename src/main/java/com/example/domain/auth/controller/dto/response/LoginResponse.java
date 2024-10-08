package com.example.domain.auth.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LoginResponse {

//    private String tokenType;

    private String accessToken;

    private String refreshToken;

    private String nickname;

    private String email;
}
