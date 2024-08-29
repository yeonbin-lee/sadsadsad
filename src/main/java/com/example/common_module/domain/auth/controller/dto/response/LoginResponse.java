package com.example.common_module.domain.auth.controller.dto.response;

import com.example.common_module.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LoginResponse {

//    private String tokenType;

    private String accessToken;

    private String refreshToken;

    private Member member;
}
