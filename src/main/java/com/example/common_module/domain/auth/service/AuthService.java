package com.example.common_module.domain.auth.service;

import com.example.common_module.domain.auth.controller.dto.request.LogoutRequest;
import com.example.common_module.domain.auth.controller.dto.response.LoginResponse;
import com.example.common_module.domain.auth.controller.dto.request.OauthMemberLoginRequest;
import com.example.common_module.domain.auth.controller.dto.request.LoginRequest;
import com.example.common_module.domain.auth.controller.dto.request.SignupRequest;

public interface AuthService {

    public LoginResponse login(LoginRequest requestDto);

    public String refreshAccessToken(String refreshToken, String email);

    public LoginResponse loginByKakao(String token);
//    public HttpHeaders kakaoLogin();

    public void signup(SignupRequest requestDto);
    /** 인가코드로 액세스 토큰 발급 */
    public String kakaoAccess(OauthMemberLoginRequest oauthMemberLoginRequest);

    public void logout(LogoutRequest request);
}
