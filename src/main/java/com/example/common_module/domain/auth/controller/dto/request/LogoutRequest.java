package com.example.common_module.domain.auth.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequest {
    private String email;
    private String accessToken;
//    private String refreshToken;
}
