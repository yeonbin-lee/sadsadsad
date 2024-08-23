package com.example.common_module.member.domain.dto;

import com.example.common_module.member.domain.entity.Gender;
import com.example.common_module.member.domain.entity.Member;
import com.example.common_module.member.domain.entity.Role;
import com.example.common_module.member.utils.MinimumAge;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
//@NoArgsConstructor
//@AllArgsConstructor
public class MemberRequestDTO {
    private Role role;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,10}$", message = "닉네임은 특수문자를 제외한 2~10자리여야 합니다.")
    private String nickname;

    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$", message = "비밀번호는 8~15자 영문, 숫자, 특수문자를 사용하세요.")
    private String password;

    @NotBlank(message = "전화번호는 필수 입력값입니다.")
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
    private String phone;

//    @NotBlank(message = "성별을 입력해주세요.")
    private Gender gender;

//    @NotBlank(message = "생년월일을 입력해주세요.")
    @MinimumAge
    private LocalDate birthday;

    public Member toEntity() {
        return Member.builder()
                .role(this.role)
                .email(this.email)
                .nickname(this.nickname)
                .password(this.password)
                .phone(this.phone)
                .gender(this.gender)
                .birthday(this.birthday)
                .build();
    }
}
