package com.example.common_module.domain.auth.service;

import com.example.common_module.domain.auth.controller.dto.response.LoginResponse;
import com.example.common_module.domain.auth.controller.dto.request.OauthMemberLoginRequest;
import com.example.common_module.domain.auth.controller.dto.request.MemberLoginRequest;
import com.example.common_module.domain.auth.controller.dto.request.MemberSignupRequest;

public interface AuthService {

    public LoginResponse login(MemberLoginRequest requestDto);

    public String refreshAccessToken(String refreshToken, String email);

    public LoginResponse loginByKakao(String token);
//    public HttpHeaders kakaoLogin();

    public void signup(MemberSignupRequest requestDto);
    /** 인가코드로 액세스 토큰 발급 */
    public String kakaoAccess(OauthMemberLoginRequest oauthMemberLoginRequest);
}
