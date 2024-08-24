//package com.example.common_module.member.domain.entity;
//
//import com.example.common_module.jwt.JwtTokenProvider;
//import jakarta.persistence.*;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//@NoArgsConstructor
//@Getter
//@Setter
//@Entity
//public class Auth {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private String tokenType;
//
//    @Column(nullable = false)
//    private String accessToken;
//
//    @Column(nullable = false)
//    private String refreshToken;
//
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id")
//    private Member member;
//
//    @Builder
//    public Auth(Member member, String tokenType, String accessToken, String refreshToken) {
//        this.member = member;
//        this.tokenType = tokenType;
//        this.accessToken = accessToken;
//        this.refreshToken = refreshToken;
//    }
//
//    public void updateAccessToken(String accessToken){
//        this.accessToken = accessToken;
//    }
//
//    public void updateRefreshToken(String refreshToken){
//        this.refreshToken = refreshToken;
//    }
//}
