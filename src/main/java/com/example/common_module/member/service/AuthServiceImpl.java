package com.example.common_module.member.service;

import com.example.common_module.jwt.CustomUserDetails;
import com.example.common_module.jwt.JwtTokenProvider;
import com.example.common_module.jwt.RefreshToken;
import com.example.common_module.jwt.RefreshTokenRepository;
import com.example.common_module.member.domain.dto.AuthRequestDTO;
import com.example.common_module.member.domain.dto.LoginJwtDTO;
import com.example.common_module.member.domain.entity.Member;
import com.example.common_module.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
    private final MemberRepository memberRepository;
//    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    /** 로그인 */
    @Transactional
    public LoginJwtDTO login(AuthRequestDTO requestDto) {
        // CHECK USERNAME AND PASSWORD
        Member member = this.memberRepository.findByEmail(requestDto.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다. email = " + requestDto.getEmail()));
        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다. email = " + requestDto.getEmail());
        }

        // GENERATE ACCESS_TOKEN AND REFRESH_TOKEN
        String accessToken = this.jwtTokenProvider.generateAccessToken(
                new UsernamePasswordAuthenticationToken(new CustomUserDetails(member), member.getPassword()));
        String refreshToken = this.jwtTokenProvider.generateRefreshToken(
                new UsernamePasswordAuthenticationToken(new CustomUserDetails(member), member.getPassword()));

        // REFRESH_TOKEN SAVE IN REDIS
        refreshTokenRepository.save(RefreshToken.builder()
                        .id(requestDto.getEmail())
                        .refresh_token(refreshToken)
                        .expiration(7200)
                        .build());

//        // 문제 코드
//        // CHECK IF AUTH ENTITY EXISTS, THEN UPDATE TOKEN
//        if (this.authRepository.existsByMember(member)) {
//            member.getAuth().updateAccessToken(accessToken);
//            member.getAuth().updateRefreshToken(refreshToken);
//            return new AuthResponseDTO(member.getAuth());
//        }

        LoginJwtDTO loginJwtDTO = LoginJwtDTO.builder()
                .member(member)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return loginJwtDTO;

        // IF NOT EXISTS AUTH ENTITY, SAVE AUTH ENTITY AND TOKEN
//        Auth auth = this.authRepository.save(Auth.builder()
//                .member(member)
//                .tokenType("Bearer")
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
//                .build());
//        return new AuthResponseDTO(auth);
    }

    /** Token 갱신 */
    @Transactional
    public String refreshAccessToken(String refreshToken, String email) {
        // CHECK IF REFRESH_TOKEN EXPIRATION AVAILABLE, UPDATE ACCESS_TOKEN AND RETURN

        // Redis 내의 리프레시 토큰과 입력받은 리프레시 토큰이 일치하는지 확인
//        if (this.jwtTokenProvider.validRefreshToken(refreshToken, authRequestDTO.getEmail())) {
//            String newAccessToken = this.jwtTokenProvider.generateAccessToken(new )
//        }
        System.out.println("222");
        Member member = memberRepository.findByEmail(email).get();


        if (this.jwtTokenProvider.validateToken(refreshToken)) {
//            Auth auth = this.authRepository.findByRefreshToken(refreshToken).orElseThrow(
//                    () -> new IllegalArgumentException("해당 REFRESH_TOKEN 을 찾을 수 없습니다.\nREFRESH_TOKEN = " + refreshToken));
            String newAccessToken = this.jwtTokenProvider.generateAccessToken(
                    new UsernamePasswordAuthenticationToken(new CustomUserDetails(member), member.getPassword()));
            return newAccessToken;
        }

        // IF NOT AVAILABLE REFRESH_TOKEN EXPIRATION, REGENERATE ACCESS_TOKEN AND REFRESH_TOKEN
        // IN THIS CASE, USER HAVE TO LOGIN AGAIN, SO REGENERATE IS NOT APPROPRIATE
        return null;
    }


//    public boolean checkDuplicatePhone(String phone){
//        return memberRepository.existByEmail(phone);
//    }
}