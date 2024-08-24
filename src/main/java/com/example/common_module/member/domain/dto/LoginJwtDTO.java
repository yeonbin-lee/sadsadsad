package com.example.common_module.member.domain.dto;

import com.example.common_module.member.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LoginJwtDTO {

//    private String tokenType;

    private String accessToken;

    private String refreshToken;

    private Member member;
}
