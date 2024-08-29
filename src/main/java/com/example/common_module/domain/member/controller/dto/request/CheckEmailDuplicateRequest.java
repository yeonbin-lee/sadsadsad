package com.example.common_module.domain.member.controller.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckEmailDuplicateRequest {

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;
}
