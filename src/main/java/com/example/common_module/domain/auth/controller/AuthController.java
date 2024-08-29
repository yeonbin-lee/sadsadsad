package com.example.common_module.domain.auth.controller;

import com.example.common_module.domain.auth.service.AuthService;
import com.example.common_module.domain.auth.controller.dto.request.OauthMemberLoginRequest;
import com.example.common_module.domain.auth.controller.dto.request.MemberLoginRequest;
import com.example.common_module.domain.auth.controller.dto.response.LoginResponse;
import com.example.common_module.domain.auth.controller.dto.request.MemberSignupRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
public class AuthController {

    /** 실제 서비스 구현할 때는 인가코드를 받는 컨트롤러는 삭제 */
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String KAKAO_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String KAKAO_REDIRECT_URI;

    private final AuthService authService;


    /** [일반] 이메일 회원가입 API */
    @PostMapping("/api/v1/auth/signup")
    public ResponseEntity<?> singUp(@RequestBody @Valid MemberSignupRequest request) {
        authService.signup(request);
        return ResponseEntity.status(HttpStatus.OK).body("User registered successfully!");
    }

    /** [일반] 로그인 API */
    @PostMapping("/api/v1/auth/login")
    public ResponseEntity<?> login(@RequestBody @Valid MemberLoginRequest request) {
        LoginResponse loginResponse = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }

    /** [카카오] 인가코드 요청 API */
    @GetMapping("/oauth/kakao/authorize")
    public String kakaoConnect() {
        StringBuffer url = new StringBuffer();
        url.append("https://kauth.kakao.com/oauth/authorize?");
        url.append("client_id="+KAKAO_CLIENT_ID);
        url.append("&redirect_uri="+KAKAO_REDIRECT_URI);
        url.append("&response_type=code");
        return "redirect:" + url.toString();
    }

    /** [카카오] 인가코드 받기 API */
    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam("code") String code) {
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * [카카오] 발급받은 인가코드로 카카오 액세스 토큰 발급
     * */
    @PostMapping("/ouath/kakao/access")
    public ResponseEntity<?> kakaoAccess(@RequestBody OauthMemberLoginRequest oauthMemberLoginRequest){
        String access_token = authService.kakaoAccess(oauthMemberLoginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(access_token);
    }

    /**
     * [카카오] 로그인
     */
    @PostMapping("/oauth/kakao/login")
    public ResponseEntity<?> loginByKakao(@RequestBody OauthMemberLoginRequest request) {
        LoginResponse response = authService.loginByKakao(request.getToken());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    /** Access 토큰갱신 API */
    @GetMapping("/api/v1/auth/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("REFRESH_TOKEN") String refreshToken, @RequestParam(value = "email") String email) {
        String newAccessToken = this.authService.refreshAccessToken(refreshToken, email);
        return ResponseEntity.status(HttpStatus.OK).body(newAccessToken);
    }

}