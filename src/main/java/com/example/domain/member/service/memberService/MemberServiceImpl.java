package com.example.domain.member.service.memberService;

import com.example.domain.auth.service.RefreshTokenService;
import com.example.domain.member.controller.dto.request.member.NicknameChangeRequest;
import com.example.domain.member.controller.dto.request.member.PwChangeRequest;
import com.example.domain.member.controller.dto.response.MemberResponse;
import com.example.domain.member.entity.Member;
import com.example.domain.member.repository.MemberRepository;
import com.example.domain.member.service.logoutService.LogoutService;
import com.example.global.config.jwt.JwtTokenProvider;
import com.example.global.exception.custom.NotEqualsPasswordException;
import com.example.global.exception.custom.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final LogoutService logoutService;


    /** Member 조회 */
    @Override
    @Transactional
    public MemberResponse findById(String accessToken) {
        Member member = findMemberById(findMemberIdByAccessToken(accessToken));
        return new MemberResponse(member);
    }


    /** 2. 비밀번호 재설정
     *  - 비밀번호를 변경하고싶을 경우
     *  1) User로부터 받은 비밀번호와 Access Token으로 찾은 password 비교
     *  2) 같을 경우 password 재설정
     * */
    public void changePassword(String accessToken, PwChangeRequest request) {

        String originPassword = request.getOriginPassword();
        String newPassword = request.getNewPassword();

        Member member = findMemberById(findMemberIdByAccessToken(accessToken));

        // member의 password와 사용자가 입력한 비밀번호 일치 확인
        if(passwordEncoder.matches(originPassword, member.getPassword())) {
            member.updatePassword(passwordEncoder.encode(newPassword));
            saveMember(member);
        } else {
            throw new NotEqualsPasswordException("비밀번호가 일치하지 않습니다.");
        }
    }


    /** Member 삭제 */
    @Override
    @Transactional
    public void delete(String accessToken) {
        Member member = findMemberById(findMemberIdByAccessToken(accessToken));

        deleteMember(member); // member 엔티티에서 삭제
        logout(accessToken, member.getEmail()); // accessToken, RefreshToken 무효화
    }


    public void logout(String accessToken, String email) {
        String token = accessToken.substring(7);
        // Redis 내의 기존 refreshToken 삭제
        if (!refreshTokenService.existsById(email)){
            // 리프레시 토큰 없다고 예외처리 날려야됨
        }
        refreshTokenService.deleteById(email);

        // access_token의 남은 유효시간 가져오기 (Seconds 단위)
        Date expirationFromToken = jwtTokenProvider.getExpirationFromToken(token);
        Date today = new Date();
        Integer sec = (int) ((expirationFromToken.getTime() - today.getTime()) / 1000);

        // accessToken을 Redis의 key 값으로 등록
        logoutService.logoutUser(token, sec); // "Bearer "삭제
    }

    /** 회원 닉네임 재설정 */
    public void changeNickname(String accessToken, NicknameChangeRequest request){
        String newNickname = request.getNewNickname();

        Member member = findMemberById(findMemberIdByAccessToken(accessToken));

        if(!memberRepository.existsByNickname(newNickname)){
            member.updateNickname(newNickname);
            memberRepository.save(member);
        } else {
            throw new DataIntegrityViolationException("중복되는 닉네임입니다.");
        }
    }

    @Transactional
    public Member findMemberById(Long memberId){
        return memberRepository.findById(memberId).orElseThrow(
                () -> new UserNotFoundException("회원 정보가 존재하지 않습니다.")
        );
    }

    public Long findMemberIdByAccessToken(String accessToken){
        return jwtTokenProvider.getUserIdFromToken(accessToken.substring(7));
    }

    public Boolean existByEmail(String email){
        return memberRepository.existsByEmail(email);
    }

    public Boolean existByPhone(String phone){
        return memberRepository.existsByPhone(phone);
    }

    public Boolean existByNickname(String nickname){
        return memberRepository.existsByNickname(nickname);
    }

    public void saveMember(Member member) {
        memberRepository.save(member);
    }

    public Member findByPhone(String phone){
        return memberRepository.findByPhone(phone).orElseThrow(
                () -> new UserNotFoundException("등록되지 않은 전화번호입니다.")
        );
    }

    public Optional<Member> findOpMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException("존재하지 않는 이메일 정보입니다.")
        );
    }

    public Member findByEmailAndPhone(String email, String phone){
        return memberRepository.findByEmailAndPhone(email, phone).orElseThrow(
                () -> new UserNotFoundException("이메일 정보와 휴대폰 정보가 일치하지 않습니다.")
        );
    }

    private void deleteMember(Member member){
        memberRepository.delete(member);
    }
}
