package com.example.common_module.domain.member.entity;

import com.example.common_module.domain.member.entity.enums.Gender;
import com.example.common_module.domain.member.entity.enums.Provider;
import com.example.common_module.domain.member.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Member {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Column
    private String nickname;

    @Column(unique = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column
    private String password;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberTerm> memberTerms;


    @Builder
    public Member(String nickname, String email, String password, String phone, Gender gender, LocalDate birthday, Role role, Provider provider) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.gender = gender;
        this.birthday = birthday;
        this.role = role;
        this.provider = provider;
    }

    public void updatePhone(String phone) {this.phone = phone;}
    public void updatePassword(String password){
        this.password = password;
    }
    public void updateNickname(String nickname){
        this.nickname = nickname;
    }

}
