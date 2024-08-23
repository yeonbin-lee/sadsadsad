package com.example.common_module.member.service;

import com.example.common_module.member.domain.dto.AuthRequestDTO;
import com.example.common_module.member.domain.dto.AuthResponseDTO;
import com.example.common_module.member.domain.dto.MemberRequestDTO;
import org.springframework.validation.Errors;

import java.util.Map;

public interface AuthService {

    public AuthResponseDTO login(AuthRequestDTO requestDto);

    public String refreshToken(String refreshToken);

}
