package com.example.common_module.member.service;

import com.example.common_module.member.domain.dto.AuthRequestDTO;
import com.example.common_module.member.domain.dto.LoginJwtDTO;
import com.example.common_module.member.domain.entity.Member;


import java.util.Map;

public interface AuthService {

    public LoginJwtDTO login(AuthRequestDTO requestDto);

    public String refreshAccessToken(String refreshToken, String email);

}
