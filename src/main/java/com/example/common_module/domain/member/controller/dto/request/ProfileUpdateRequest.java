package com.example.common_module.domain.member.controller.dto.request;

import com.example.common_module.domain.member.entity.enums.Choice;
import lombok.Getter;

@Getter
public class ProfileUpdateRequest {

    private Long profile_id;

    private Choice pregnancy;

    private Choice smoking;

    private Choice hypertension;

    private Choice diabetes;
}
