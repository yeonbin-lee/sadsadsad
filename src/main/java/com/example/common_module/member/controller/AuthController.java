package com.example.common_module.member.controller;

import com.example.common_module.member.domain.dto.AuthRequestDTO;
import com.example.common_module.member.domain.dto.AuthResponseDTO;
import com.example.common_module.member.domain.dto.CheckEmailDuplicateDTO;
import com.example.common_module.member.domain.dto.MemberRequestDTO;
import com.example.common_module.member.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    /** 로그인 API */
    @PostMapping("/api/v1/auth/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequestDTO requestDto) {
        AuthResponseDTO responseDto = this.authService.login(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }



    /** 토큰갱신 API */
    @GetMapping("/api/v1/auth/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("REFRESH_TOKEN") String refreshToken) {
        String newAccessToken = this.authService.refreshToken(refreshToken);
        return ResponseEntity.status(HttpStatus.OK).body(newAccessToken);
    }
}