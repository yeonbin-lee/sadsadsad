package com.example.domain.auth.service;

import com.example.domain.auth.controller.dto.request.*;
import com.example.domain.auth.controller.dto.response.FindEmailResponse;
import com.example.domain.auth.controller.dto.response.LoginByAccessTokenResponse;
import com.example.domain.auth.controller.dto.response.LoginResponse;
import com.example.domain.auth.controller.vo.TokenResponse;
import com.example.domain.auth.util.UUIDUtil;
import com.example.domain.coolsms.entity.Sms;
import com.example.domain.coolsms.service.SmsService;
import com.example.domain.member.controller.vo.KakaoInfo;
import com.example.domain.member.controller.vo.KakaoToken;
import com.example.domain.member.entity.Member;
import com.example.domain.member.entity.MemberTermsAgreement;
import com.example.domain.member.entity.TermsCondition;
import com.example.domain.member.entity.enums.Gender;
import com.example.domain.member.entity.enums.Provider;
import com.example.domain.member.entity.enums.Role;
import com.example.domain.member.service.consentService.ConsentService;
import com.example.domain.member.service.fcm.FcmService;
import com.example.domain.member.service.memberService.MemberService;
import com.example.domain.member.service.profileService.ProfileService;
import com.example.domain.member.service.terms.MemberTermsAgreementService;
import com.example.domain.member.service.terms.TermsConditionService;
import com.example.global.config.jwt.CustomUserDetails;
import com.example.global.config.jwt.JwtTokenProvider;
import com.example.global.config.jwt.RefreshToken;
import com.example.global.exception.custom.SocialLoginException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SmsService smsService;
    private final MemberService memberService;
    private final ProfileService profileService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final FcmService fcmService;

    private final TermsConditionService termsConditionService;
    private final MemberTermsAgreementService memberTermsAgreementService;
    private final ConsentService consentService;

    /** 이메일 중복체크 */
    public Boolean checkDuplicateEmail(String email){
        return memberService.existByEmail(email);
    }

    /** 전화번호 중복체크 */
    public Boolean checkDuplicatePhone(String phone) {
        return memberService.existByPhone(phone);
    }

    /** 닉네임 중복체크 */
    public Boolean checkDuplicateNickname(String nickname) {
        return memberService.existByNickname(nickname);
    }



    /** [일반] 이메일 회원가입 API
     * 이메일, 전화번호, 닉네임 중복 체크
     * 중복 없을 시 member 저장
     * 프로필 저장 Version
     * */
    @Transactional
    public void signup(SignupRequest request) {

        // 인증되지않은 전화번호
        if(!isTokenPhone(request.getPhone())){
            throw new DataIntegrityViolationException("잘못된 요청입니다.");
        }

        // CHECK EMAIL, PHONE, NICKNAME DUPLICATE
        if(memberService.existByEmail(request.getEmail())){
            throw new DataIntegrityViolationException("중복되는 이메일입니다.");
        }

        if(memberService.existByPhone(request.getPhone())){
            throw new DataIntegrityViolationException("중복되는 전화번호입니다.");
        }

        if(memberService.existByNickname(request.getNickname())){
            throw new DataIntegrityViolationException("중복되는 닉네임입니다.");
        }
        // 필수 약관 ID 목록
        List<Long> requiredTermsIds = List.of(1L, 2L, 3L);

        // 사용자가 동의한 약관 ID 리스트
        List<Long> agreedTermsIds = request.getAgreedTermsIds();

        // 필수 약관에 동의했는지 확인
        if (!agreedTermsIds.containsAll(requiredTermsIds)) {
            throw new IllegalArgumentException("모든 필수 약관에 동의해야 합니다.");
        }

        // 회원가입 후 Redis에 저장된 전화번호 인증 정보를 삭제
        deletePhoneNumberVerification(request.getPhone());

        Member member = Member.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .phone(request.getPhone())
                .gender(request.getGender())
                .password(passwordEncoder.encode(request.getPassword()))
                .birthday(request.getBirthday())
                .createdAt(LocalDate.now())
                .role(Role.ROLE_USER)
                .provider(Provider.NORMAL)
                .build();

        // 기본 프로필 등록
        profileService.registerMainProfile(member);


        // SAVE MEMBER ENTITY
        memberService.saveMember(member);


        // 동의한 필수 약관 정보 저장
        for (Long termsId : agreedTermsIds) {
            TermsCondition termsCondition = termsConditionService.findTermsConditionById(termsId);

            memberTermsAgreementService.saveMemberTermsAgreement(
                    MemberTermsAgreement.builder()
                            .member(member)
                            .termsCondition(termsCondition)
                            .isAgreed(true)
                            .agreedAt(LocalDate.now())
                            .build());
        }


        // 선택 약관 정보 저장 - 마케팅
        consentService.saveMarketingConsent(member, request.getMarketingConsent());

        // 선택 약관 정보 저장 - 시스템
        consentService.saveSystemConsent(member, request.getSystemConsent());
    }



    /**
     * 전화번호로 이메일 찾기
     * 1) CoolSms를 통해 User에게 인증코드 전송
     * 2) 받은 인증코드를 Redis 내에 (key: 전화번호, value:인증코드) 형태로 저장
     * 3) Redis 내에 인증코드가 존재한다면 email 반환
     * @return
     */
    public FindEmailResponse findEmailByPhone(FindEmailByPhoneRequest request) {

        // 인증되지않은 전화번호
        if(!isTokenPhone(request.getPhone())){
            throw new DataIntegrityViolationException("잘못된 요청입니다.");
        }

        // Redis에 저장된 전화번호 인증 정보를 삭제
        deletePhoneNumberVerification(request.getPhone());

        Member member = memberService.findByPhone(request.getPhone());
        smsService.deletePhone(request.getPhone());
        FindEmailResponse findEmailResponse = FindEmailResponse.builder()
                .provider(member.getProvider())
                .email(member.getEmail())
                .build();

        return findEmailResponse;
    }

    // Redis 내에 Key(전화번호)가 존재하는지 확인
    private Sms verifyRedisByPhone(String phone){
        Sms sms = smsService.findSmsByPhone(phone);
        smsService.deletePhone(phone);
        return sms;
    }


    /** 1. 비밀번호 재설정
     *  - 비밀번호를 잊어버렸을 경우
     *  1) coolSms를 통해 전화번호로 인증코드 전송
     *  2) 인증코드를 Redis에 저장한 후, (key: 전화번호, value:인증코드) 형태로 저장
     *  3) Redis 내에 인증코드가 존재한다면 password 재설정
     * */
    public void findPassword(PwFindRequest request) {

        // 인증되지않은 전화번호
        if(!isTokenPhone(request.getPhone())){
            throw new DataIntegrityViolationException("잘못된 요청입니다.");
        }
        // Redis에 저장된 전화번호 인증 정보를 삭제
        deletePhoneNumberVerification(request.getPhone());

        Member member = memberService.findByEmailAndPhone(request.getEmail(), request.getPhone());
        member.updatePassword(passwordEncoder.encode(request.getPassword()));
        memberService.saveMember(member);
    }


    /** [일반] 로그인 API */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        // CHECK EMAIL AND PASSWORD

        Member member = memberService.findByEmail(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        // FCM Token 갱신
        fcmService.updateFCMToken(member, request.getFcmToken());

        // GENERATE ACCESS_TOKEN AND REFRESH_TOKEN
        String accessToken = jwtTokenProvider.generateAccessToken(
                new UsernamePasswordAuthenticationToken(new CustomUserDetails(member), member.getPassword()));
        String refreshToken = jwtTokenProvider.generateRefreshToken(
                new UsernamePasswordAuthenticationToken(new CustomUserDetails(member), member.getPassword()));


        // REFRESH_TOKEN SAVE IN REDIS
        refreshTokenService.saveRefreshToken(
                RefreshToken.builder()
                        .id(request.getEmail())
                        .refresh_token(refreshToken)
                        .expiration(14) // 2주
                        .build()
        );

        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .nickname(member.getNickname())
                .email(member.getEmail())
                .build();

        return loginResponse;
    }

    public LoginByAccessTokenResponse loginByAccessToken(LoginByAccessTokenRequest request) {
        Long memberId = memberService.findMemberIdByAccessToken(request.getAccessToken());
        Member member = memberService.findMemberById(memberId);

        return LoginByAccessTokenResponse.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();

    }



    /** [일반] 어드민 로그인 API */
    @Transactional
    public LoginResponse adminLogin(LoginRequest request) {
        // CHECK EMAIL AND PASSWORD

        Member member = memberService.findByEmail(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        // GENERATE ACCESS_TOKEN AND REFRESH_TOKEN
        String accessToken = jwtTokenProvider.generateAccessToken(
                new UsernamePasswordAuthenticationToken(new CustomUserDetails(member), member.getPassword()));
        String refreshToken = jwtTokenProvider.generateRefreshToken(
                new UsernamePasswordAuthenticationToken(new CustomUserDetails(member), member.getPassword()));


        // REFRESH_TOKEN SAVE IN REDIS
        refreshTokenService.saveRefreshToken(
                RefreshToken.builder()
                        .id(request.getEmail())
                        .refresh_token(refreshToken)
                        .expiration(14) // 2주
                        .build()
        );

        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .nickname(member.getNickname())
                .email(member.getEmail())
                .build();

        return loginResponse;
    }

    /** Token 갱신 */
    @Transactional
    public String refreshAccessToken(String refreshToken, RefreshRequest request) {

        String email = request.getEmail();
        // CHECK IF REFRESH_TOKEN EXPIRATION AVAILABLE, UPDATE ACCESS_TOKEN AND RETURN
        Member member = memberService.findByEmail(email);
        if (jwtTokenProvider.validateToken(refreshToken)) {
            String newAccessToken = jwtTokenProvider.generateAccessToken(
                    new UsernamePasswordAuthenticationToken(new CustomUserDetails(member), member.getPassword()));
            return newAccessToken;
        }
        return null;
    }

    /**
     * redis에 인증된 전화번호인지 체크
     * */
    public boolean isTokenPhone(String phone) {
        // Redis에 해당 번호가 존재하는지 확인
        return Boolean.TRUE.equals(redisTemplate.hasKey(getRedisKeyForToken(phone)));
    }
    private String getRedisKeyForToken(String token) {
        return "verified_phone:" + token;
    }

    // 회원가입 후 Redis에 저장된 전화번호 인증 정보를 삭제
    public void deletePhoneNumberVerification(String phone) {
        // Redis에서 해당 전화번호와 연결된 인증 정보를 삭제
        String key = "verified_phone:" + phone;
        redisTemplate.delete(key);  // 해당 키를 삭제
    }

    // 4자리 UUID를 닉네임 뒤에 붙여서 생성하는 메서드
    private String generateNicknameWithUUID(String baseNickname) {
        String uuid4 = UUIDUtil.generate4CharUUID();
        return baseNickname + uuid4;
    }
}
