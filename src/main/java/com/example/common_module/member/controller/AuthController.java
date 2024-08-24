package com.example.common_module.member.controller;

import com.example.common_module.member.domain.dto.*;
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
        LoginJwtDTO loginJwtDTO = this.authService.login(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(loginJwtDTO);
    }


    /** 토큰갱신 API */
    @GetMapping("/api/v1/auth/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("REFRESH_TOKEN") String refreshToken, @RequestParam(value = "email") String email) {
        System.out.println("111");
        String newAccessToken = this.authService.refreshAccessToken(refreshToken, email);
        return ResponseEntity.status(HttpStatus.OK).body(newAccessToken);
    }
}