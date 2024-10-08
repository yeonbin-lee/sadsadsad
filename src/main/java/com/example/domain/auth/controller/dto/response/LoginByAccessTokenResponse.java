package com.example.domain.auth.controller.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginByAccessTokenResponse {

    private String nickname;

    private String email;
}
