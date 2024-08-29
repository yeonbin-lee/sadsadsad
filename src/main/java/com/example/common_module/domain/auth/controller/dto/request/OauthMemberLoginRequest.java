package com.example.common_module.domain.auth.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OauthMemberLoginRequest {
//    private String provider; // 지금은 kakao만
    private String token;
}
