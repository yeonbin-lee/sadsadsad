package com.example.domain.auth.controller;

import com.example.domain.auth.controller.dto.request.*;
import com.example.domain.auth.controller.dto.response.FindEmailResponse;
import com.example.domain.auth.controller.dto.response.LoginByAccessTokenResponse;
import com.example.domain.auth.controller.dto.response.LoginResponse;
import com.example.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "인증/인가 API", description = "인증/인가에 관한 코드입니다.")
@RequestMapping("/module-common/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/health-check")
    @Operation(summary = "AWS ACM 인증용")
    public ResponseEntity<String> healthCheck() {
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }


    /**
     * [일반] 이메일 회원가입 API
     * @param request - role "ROLE_USER"으로 고정
     * @param request - provider 사용자가 가입한 채널, enum(NORMAL, KAKAO)
     * @param request - email, nickname, password, phone 사용자가 입력
     * @param request - gender 사용자가 입력한 성별, enum(MALE, FEMALE, NO_INFO)
     * @param request - birthday 사용자가 입력한 생년월일, YYYY-mm-dd 형식 사용
     * @return response - provider, email
     * */
    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "SMS verify가 선행되어야 함")
    @Parameter(name = "agreedTermsIds", description = "필수 항목 동의 여부로 현재는 '[1,2,3]'을 넣어주면 된다.")
    public ResponseEntity<?> singUp(@RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.status(HttpStatus.OK).body("User registered successfully!");
    }

    /**
     * [일반] 이메일 중복체크 API
     * @param email
     * @return response - boolean (true - 이메일이 이미 존재할 경우, false - 사용할 수 있는 이메일)
     * */
    @PostMapping("/email/duplicate")
    @Operation(summary = "이메일 중복 체크", description = "return: Boolean")
    public ResponseEntity<?> checkEmailDuplicate(@RequestParam String email){
        Boolean checkEmailDuplicate = authService.checkDuplicateEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(checkEmailDuplicate);
    }

    /**
     * [일반] 전화번호 중복체크 API
     * @param phone
     * @return response - boolean
     * */
    @PostMapping("/phone/duplicate")
    @Operation(summary = "전화번호 중복 체크", description = "return: Boolean")
    public ResponseEntity<?> checkPhoneDuplicate(@RequestParam String phone){
        Boolean checkPhoneDuplicate = authService.checkDuplicatePhone(phone);
        return ResponseEntity.status(HttpStatus.OK).body(checkPhoneDuplicate);
    }

    /**
     * [일반] 닉네임 중복체크 API
     * @param nickname
     * @return response - boolean
     * */
    @PostMapping("/nickname/duplicate")
    @Operation(summary = "닉네임 중복 체크", description = "return: Boolean")
    public ResponseEntity<?> checkNicknameDuplicate(@RequestParam String nickname){
        Boolean checkNicknameDuplicate = authService.checkDuplicateNickname(nickname);
        return ResponseEntity.status(HttpStatus.OK).body(checkNicknameDuplicate);
    }



    /**
     * [일반] 비밀번호 재설정 API by phone - 비밀번호를 잊어버렸을 경우
     * @param request - phone 사용자의 전화번호
     * @param request - password 비밀번호로 설정할 새로운 비밀번호
     * */
    @PutMapping("/phone/change/password")
    @Operation(summary = "비밀번호 재설정", description = "SMS verify가 선행되어야 함")
    public ResponseEntity<?> findPassword(@RequestBody PwFindRequest request){
        authService.findPassword(request);
        return ResponseEntity.status(HttpStatus.OK).body("Password changed successfully!");
    }

    /**
     * [일반] 로그인 API
     * @param request - 사용자가 입력한 email, password
     * @return response - accessToken, refreshToken, Member 객체
     * */
    @PostMapping("/login")
    @Operation(summary = "로그인")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }

    /**
     * 액세스 토큰으로 사용자 정보 요청
     */
    @PostMapping("/access-token/login")
    @Operation(summary = "액세스 토큰으로 로그인")
    public ResponseEntity<?> loginByAccessToken(@RequestBody LoginByAccessTokenRequest request) {
        LoginByAccessTokenResponse response = authService.loginByAccessToken(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/admin/login")
    @Operation(summary = "어드민 로그인", description = "이 후에 시간 여유가 있다면 2차 인증 기능 추가 예정")
    public ResponseEntity<?> adminLogin(@RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.adminLogin(request);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }



    /**
     * 이메일 찾기 By 전화번호 API
     * @param request phone - 사용자 전화번호
     * @return response - provider, email
     */
    @GetMapping("/find/email")
    @Operation(summary = "이메일 찾기", description = "전화번호로 이메일 찾기")
    public ResponseEntity<?> findEmailByPhone(@RequestBody FindEmailByPhoneRequest request){
        FindEmailResponse response = authService.findEmailByPhone(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    /**
     * Access 토큰갱신 API
     * */
    @GetMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("REFRESH_TOKEN") String refreshToken, @RequestBody RefreshRequest request) {
        String newAccessToken = authService.refreshAccessToken(refreshToken, request);
        return ResponseEntity.status(HttpStatus.OK).body(newAccessToken);
    }


}
