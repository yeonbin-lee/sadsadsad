package com.example.domain.auth.controller.dto.request;

import lombok.Getter;

@Getter
public class LoginByAccessTokenRequest {

    private String accessToken;
}
