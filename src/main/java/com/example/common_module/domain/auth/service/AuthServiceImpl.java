package com.example.common_module.domain.auth.service;

import com.example.common_module.domain.auth.controller.dto.request.LogoutRequest;
import com.example.common_module.domain.member.controller.vo.KakaoInfo;
import com.example.common_module.domain.auth.controller.dto.request.SignupRequest;
import com.example.common_module.domain.member.controller.vo.KakaoToken;
import com.example.common_module.domain.auth.controller.dto.response.LoginResponse;
import com.example.common_module.domain.member.entity.Logout;
import com.example.common_module.domain.member.entity.Profile;
import com.example.common_module.domain.member.entity.enums.Provider;
import com.example.common_module.domain.member.repository.*;
import com.example.common_module.global.config.jwt.CustomUserDetails;
import com.example.common_module.global.config.jwt.JwtTokenProvider;
import com.example.common_module.global.config.jwt.RefreshToken;
import com.example.common_module.global.config.jwt.RefreshTokenRepository;
import com.example.common_module.domain.auth.controller.dto.request.OauthMemberLoginRequest;
import com.example.common_module.domain.auth.controller.dto.request.LoginRequest;
import com.example.common_module.domain.member.entity.enums.Gender;
import com.example.common_module.domain.member.entity.Member;
import com.example.common_module.domain.member.entity.enums.Role;
import com.example.common_module.domain.auth.service.helper.KakaoClient;
import com.example.common_module.global.exception.custom.SocialLoginException;
import com.example.common_module.global.exception.custom.UserNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final MemberRepository memberRepository;
    private final TermRepository termRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final KakaoClient kakaoClient;
    private final MemberTermRepository memberTermRepository;
    private final LogoutRepository logoutRepository;
//    private final ProfileRepository profileRepository;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String KAKAO_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String KAKAO_REDIRECT_URI;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String KAKAO_CLIENT_SECRET;


//    /** [일반] 이메일 회원가입 API
//     * 이메일, 전화번호, 닉네임 중복 체크
//     * 중복 없을 시 member 저장
//     * */
//    @Transactional
//    public void signup(SignupRequest request) {
//
//        // CHECK EMAIL, PHONE, NICKNAME DUPLICATE
//        if(memberRepository.existsByEmail(request.getEmail())){
//            throw new DataIntegrityViolationException("중복되는 이메일입니다.");
//        }
//
//        if(memberRepository.existsByPhone(request.getPhone())){
//            throw new DataIntegrityViolationException("중복되는 전화번호입니다.");
//        }
//
//        if(memberRepository.existsByNickname(request.getNickname())){
//            throw new DataIntegrityViolationException("중복되는 닉네임입니다.");
//        }
//
//        // SAVE MEMBER ENTITY
//        Member member = Member.builder()
//                .email(request.getEmail())
//                .nickname(request.getNickname())
//                .phone(request.getPhone())
//                .gender(request.getGender())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .birthday(request.getBirthday())
//                .role(Role.ROLE_USER)
//                .provider(Provider.NORMAL)
//                .build();
//
//        memberRepository.save(member);
//    }

    /** [일반] 이메일 회원가입 API
     * 이메일, 전화번호, 닉네임 중복 체크
     * 중복 없을 시 member 저장
     * 프로필 저장 Version
     * */
    @Transactional
    public void signup(SignupRequest request) {

        // CHECK EMAIL, PHONE, NICKNAME DUPLICATE
        if(memberRepository.existsByEmail(request.getEmail())){
            throw new DataIntegrityViolationException("중복되는 이메일입니다.");
        }

        if(memberRepository.existsByPhone(request.getPhone())){
            throw new DataIntegrityViolationException("중복되는 전화번호입니다.");
        }

        if(memberRepository.existsByNickname(request.getNickname())){
            throw new DataIntegrityViolationException("중복되는 닉네임입니다.");
        }

        // SAVE MEMBER ENTITY
        Member member = Member.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .phone(request.getPhone())
                .gender(request.getGender())
                .password(passwordEncoder.encode(request.getPassword()))
                .birthday(request.getBirthday())
                .role(Role.ROLE_USER)
                .provider(Provider.NORMAL)
                .build();

//        Profile profile = Profile.builder()
//                .member(member)
//                .nickname(request.getNickname())
//                .gender(request.getGender())
//                .birthday(request.getBirthday())
//                .owner(Boolean.TRUE)
//                .build();

        memberRepository.save(member);
//        profileRepository.save(profile);
    }


//    /** [일반] 이메일 회원가입 API
//     * 이메일, 전화번호, 닉네임 중복 체크
//     * 중복 없을 시 member 저장
//     * 이용약관 추가 ver
//     * */
//    @Transactional
//    public void signup(MemberSignupRequest request) {
//
//        // CHECK EMAIL, PHONE, NICKNAME DUPLICATE
//        if(memberRepository.existsByEmail(request.getEmail())){
//            throw new DataIntegrityViolationException("중복되는 이메일입니다.");
//        }
//
//        if(memberRepository.existsByPhone(request.getPhone())){
//            throw new DataIntegrityViolationException("중복되는 전화번호입니다.");
//        }
//
//        if(memberRepository.existsByNickname(request.getNickname())){
//            throw new DataIntegrityViolationException("중복되는 닉네임입니다.");
//        }
//
//        // BUILD MEMBER ENTITY
//        Member member = Member.builder()
//                .email(request.getEmail())
//                .nickname(request.getNickname())
//                .phone(request.getPhone())
//                .gender(request.getGender())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .birthday(request.getBirthday())
//                .role(Role.ROLE_USER)
//                .provider(Provider.NORMAL)
//                .build();
//
//
//        // 필수 약관 처리
//        List<Term> mandatoryTerms = termRepository.findByMandatoryTrue();
//
//        Map<Long, Boolean> termAcceptancesMap = request.getTermAcceptances().stream()
//                .collect(Collectors.toMap(TermAcceptance::getTermId, TermAcceptance::isAccepted));
//
//        List<MemberTerm> memberTerms = new ArrayList<>();
//
//        for (Term term : mandatoryTerms) {
//            Boolean accepted = termAcceptancesMap.get(term.getId());
//
//            if (accepted == null || !accepted) {
//                throw new RuntimeException("You must accept all mandatory terms and conditions.");
//            }
//
//            MemberTerm memberTerm= MemberTerm.builder()
//                    .member(member)
//                    .term(term)
//                    .accepted(accepted)
//                    .build();
//
//            memberTerms.add(memberTerm);
//        }
//
//
//        // 선택 약관 처리
//        for (TermAcceptance termAcceptance : request.getTermAcceptances()) {
//            if (!termAcceptancesMap.containsKey(termAcceptance.getTermId())) {
//                continue;
//            }
//
//            Term term = termRepository.findById(termAcceptance.getTermId())
//                    .orElseThrow(() -> new RuntimeException("Invalid term ID"));
//
//            if (!term.isMandatory()) {
//                MemberTerm memberTerm= MemberTerm.builder()
//                        .member(member)
//                        .term(term)
//                        .accepted(termAcceptance.isAccepted())
//                        .build();
//
//                memberTerms.add(memberTerm);
//            }
//        }
//
//        member.setMemberTerms(memberTerms);
//
//        memberRepository.save(member);
//    }


    /** [일반] 로그인 API */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        // CHECK EMAIL AND PASSWORD
        Member member = this.memberRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new UserNotFoundException(request.getEmail() + "은 존재하지 않는 이메일 정보입니다."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        // GENERATE ACCESS_TOKEN AND REFRESH_TOKEN
        String accessToken = this.jwtTokenProvider.generateAccessToken(
                new UsernamePasswordAuthenticationToken(new CustomUserDetails(member), member.getPassword()));
        String refreshToken = this.jwtTokenProvider.generateRefreshToken(
                new UsernamePasswordAuthenticationToken(new CustomUserDetails(member), member.getPassword()));


        // REFRESH_TOKEN SAVE IN REDIS
        refreshTokenRepository.save(RefreshToken.builder()
                .id(request.getEmail())
                .refresh_token(refreshToken)
                .expiration(14) // 2주
                .build());

        LoginResponse loginResponse = LoginResponse.builder()
                .member(member)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return loginResponse;
    }


    /**
     * [카카오] 발급받은 인가코드로 카카오 액세스 토큰 발급
     * */
    public String kakaoAccess(OauthMemberLoginRequest oauthMemberLoginRequest) {
        String access_Token = "";
        String refresh_Token = "";
        String code = oauthMemberLoginRequest.getToken();
        String strUrl = "https://kauth.kakao.com/oauth/token"; // 토큰 요청 보낼 주소
        KakaoToken kakaoToken = new KakaoToken(); // 요청받을 객체

        try {
            URL url = new URL(strUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // url Http 연결 생성

            // POST 요청
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);// outputStreamm으로 post 데이터를 넘김

            // 파라미터 세팅
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();

            // 0번 파라미터 grant_type 입니다 authorization_code로 고정
            sb.append("grant_type=authorization_code");

            // 1번 파라미터 client_id입니다.
            sb.append("&client_id=" + KAKAO_CLIENT_ID);

            // 2번 파라미터 redirect_uri입니다.
            sb.append("&redirect_uri=" + KAKAO_REDIRECT_URI);

            // 3번 파라미터 code
            sb.append("&code=" + code);

            sb.append("&client_secret=" + KAKAO_CLIENT_SECRET);

            bw.write(sb.toString());
            bw.flush();// 실제 요청을 보내는 부분

            // 실제 요청을 보내는 부분, 결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            log.info("responsecode(200이면성공): {}", responseCode);

            // 요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            log.info("response body: {}", result);

            // Jackson으로 json 파싱할 것임
            ObjectMapper mapper = new ObjectMapper();

            // kakaoToken에 result를 KakaoToken.class 형식으로 변환하여 저장
            kakaoToken = mapper.readValue(result, KakaoToken.class);
            System.out.println(kakaoToken);

            // api호출용 access token
            access_Token = kakaoToken.getAccess_token();

            // access 토큰 만료되면 refresh token사용(유효기간 더 김)
            refresh_Token = kakaoToken.getRefresh_token();

            log.info(access_Token);
            log.info(refresh_Token);

            br.close();
            bw.close();
        } catch (IOException e) {
            throw new SocialLoginException("[카카오] 액세스 토큰을 발급받는 데 실패했습니다.");
//            throw new IOException("[카카오] 액세스 토큰을 요청하는 데 실패했습니다.");
        }
        return access_Token;
    }


    /**
     * [카카오] 로그인
     */
    public LoginResponse loginByKakao(String token) {
        System.out.println("[카카오] 받은 액세스 토큰="+ token);
        Member member = signInByProvider(token);
        Optional<Member> findUser = memberRepository.findByEmail(member.getEmail());
        if (findUser.isEmpty()) { // 최초 로그인이라면 회원가입 시키기
            memberRepository.save(member);
        }
        return createAndSaveToken(member);
    }

    private Member signInByProvider(String token) {

        // 카카오 로그인
        KakaoInfo memberInfo = kakaoClient.getUserInfo("Bearer " + token);
        String email = memberInfo.getKakaoAccount().getEmail();
        String nickname = memberInfo.getKakaoAccount().getProfile().getNickname();
        String gender = memberInfo.getKakaoAccount().getGender();
        // 생년
        String birthyear = memberInfo.getKakaoAccount().getBirthyear();
        // 월일
        String birthday = memberInfo.getKakaoAccount().getBirthday();

        // LocalDate 타입 변환 형식 지정 (yyyy-mm-dd)
        String birth_str = birthyear + "-" + birthday.substring(0,2) + "-" + birthday.substring(2,4);
        LocalDate birth = LocalDate.parse(birth_str, DateTimeFormatter.ISO_DATE);

        String password = socialRandomPassword(email);
        Member member = new Member(email,nickname,password, "", Gender.valueOf(gender.toUpperCase()), birth, Role.ROLE_USER, Provider.KAKAO);
        return member;
    }

    private LoginResponse createAndSaveToken(Member member) {
        String accessToken = jwtTokenProvider.generateAccessToken(new UsernamePasswordAuthenticationToken(new CustomUserDetails(member), member.getPassword()));
        String refreshToken = jwtTokenProvider.generateRefreshToken(new UsernamePasswordAuthenticationToken(new CustomUserDetails(member), member.getPassword()));
        refreshTokenRepository.save(RefreshToken.builder()
                .id(member.getEmail())
                .refresh_token(refreshToken)
                .expiration(14)
                .build());

        LoginResponse loginResponse = LoginResponse.builder()
                .member(member)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return loginResponse;
    }

    private String socialRandomPassword(String email) {
        String systemMil = String.valueOf(System.currentTimeMillis());
        return passwordEncoder.encode(email + systemMil);
    }


    /** Token 갱신 */
    @Transactional
    public String refreshAccessToken(String refreshToken, String email) {
        // CHECK IF REFRESH_TOKEN EXPIRATION AVAILABLE, UPDATE ACCESS_TOKEN AND RETURN
        Member member = memberRepository.findByEmail(email).get();
        if (this.jwtTokenProvider.validateToken(refreshToken)) {
            String newAccessToken = this.jwtTokenProvider.generateAccessToken(
                    new UsernamePasswordAuthenticationToken(new CustomUserDetails(member), member.getPassword()));
            return newAccessToken;
        }
        return null;
    }

    public void logout(LogoutRequest request) {
        String email = request.getEmail();
        String accessToken = request.getAccessToken();

        // Redis 내의 기존 refreshToken 삭제
        if (!refreshTokenRepository.existsById(email)){
            // 리프레시 토큰 없다고 예외처리 날려야됨
        }
        refreshTokenRepository.deleteById(email);


        // access_token의 남은 유효시간 가져오기 (Seconds 단위)
        Date expirationFromToken = jwtTokenProvider.getExpirationFromToken(accessToken);
        Date today = new Date();
        Integer sec = (int) ((expirationFromToken.getTime() - today.getTime()) / 1000);

        // access_token을 Redis의 key 값으로 등록
        logoutRepository.save(
                Logout.builder()
                        .id(accessToken)
                        .data("logout")
                        .expiration(sec)
                        .build()
                );
    }
}