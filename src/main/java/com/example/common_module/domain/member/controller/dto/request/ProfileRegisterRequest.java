package com.example.common_module.domain.member.controller.dto.request;

import com.example.common_module.domain.member.entity.Member;
import com.example.common_module.domain.member.entity.Profile;
import com.example.common_module.domain.member.entity.enums.Choice;
import com.example.common_module.domain.member.entity.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ProfileRegisterRequest {

    private String email;

    private String nickname;

    private Gender gender;

    private Boolean owner;

    private LocalDate birthday;

    private Choice pregnancy;

    private Choice smoking;

    private Choice hypertension;

    private Choice diabetes;

//    public Profile toEntity() {
//        return Profile.builder()
//                .nickname(nickname)
//                .owner(Boolean.FALSE)
//                .gender(gender)
//                .birthday(birthday)
//                .pregnancy(pregnancy)
//                .smoking(smoking)
//                .hypertension(hypertension)
//                .diabetes(diabetes)
//                .build();
//    }

}
