package com.example.common_module.member.service;

import com.example.common_module.coolsms.model.entity.Sms;
import com.example.common_module.coolsms.repository.SmsRepository;
import com.example.common_module.coolsms.service.SmsService;
import com.example.common_module.exception.custom.NotEqualsCodeException;
import com.example.common_module.exception.custom.UserNotFoundException;
import com.example.common_module.member.domain.dto.FindEmailDTO;
import com.example.common_module.member.domain.dto.MemberRequestDTO;
import com.example.common_module.member.domain.entity.Member;
import com.example.common_module.member.domain.dto.MemberResponseDTO;
import com.example.common_module.member.domain.dto.MemberUpdateDTO;
import com.example.common_module.member.domain.entity.Role;
import com.example.common_module.member.repository.FindEmailRepository;
import com.example.common_module.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final FindEmailRepository findEmailRepository;
    private final SmsRepository smsRepository;
    private final SmsService smsService;

    /**
     * 회원가입
     * @param requestDto
     * 이미 존재하는 전화번호, 이메일일 경우
     */
    @Transactional
    public void signup(MemberRequestDTO requestDto) {

        // CHECK EMAIL, PHONE, Nickname DUPLICATE
        if(memberRepository.existsByEmail(requestDto.getEmail())){
//            throw new IllegalArgumentException("중복되는 이메일입니다.");
            throw new DataIntegrityViolationException("중복되는 이메일입니다.");
        }

        if(memberRepository.existsByPhone(requestDto.getPhone())){
            throw new DataIntegrityViolationException("중복되는 전화번호입니다.");
        }

        if(memberRepository.existsByNickname(requestDto.getNickname())){
            throw new DataIntegrityViolationException("중복되는 닉네임입니다.");
        }


        // SAVE USER ENTITY
        requestDto.setRole(Role.ROLE_USER);
        requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        this.memberRepository.save(requestDto.toEntity());
    }

    /** User 조회 */
    @Override
    @Transactional
    public MemberResponseDTO findById(Long id) {
        Member member = this.memberRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. member_id = " + id));
        return new MemberResponseDTO(member);
    }

    /** User 수정 */
    @Override
    @Transactional
    public void update(Long id, MemberUpdateDTO requestDto) {
        Member member = this.memberRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. member_id = " + id));
        requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        System.out.println("encode pw=" + requestDto.getPassword());
        member.updateNickname(requestDto.getNickname());
        member.updatePassword(requestDto.getPassword());
        member.updatePhone(requestDto.getPhone());
        System.out.println("2");
    }

    /** User 삭제 */
    @Override
    @Transactional
    public void delete(Long id) {
        Member member = this.memberRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. member_id = " + id));
        this.memberRepository.delete(member);
    }

    /** 이메일 중복체크 */
    public boolean checkDuplicateEmail(String email){
        return memberRepository.existsByEmail(email);
    }

    /** 1. 비밀번호 재설정
     *  - 비밀번호를 잊어버렸을 경우
     *  1) coolSms를 통해 전화번호로 인증코드 전송
     *  2) 인증코드를 Redis에 저장한 후, (key: 전화번호, value:인증코드) 형태로 저장
     *  3) Redis 내에 인증코드가 존재한다면 password 재설정
     * */

    /** 2. 비밀번호 재설정
     *  - 비밀번호를 변경하고싶을 경우
     *  1) User로부터 받은 비밀번호와 Access Token으로 찾은 password 비교
     *  2) 같을 경우 password 재설정
     * */


    /**
     * 1) CoolSms를 통해 User에게 인증코드 전송
     * 2) 받은 인증코드를 Redis내에 (key: 전화번호, value:인증코드) 형태로 저장
     * 3) Redis 내에 인증코드가 존재한다면 email 반환
     * @return
     */
    public String findEmailByPhone(FindEmailDTO findEmailDTO) {

        String phoneNum = findEmailDTO.getPhoneNum();

        // Redis 내에 Key(전화번호)가 존재하는지 확인
        Sms sms = smsRepository.findById(phoneNum).orElseThrow(
                () -> new NullPointerException("등록되지 않은 전화번호입니다.")
        );

        // 타임아웃이 짧기 때문에 굳이 삭제할 필요는 없을 듯
//        smsRepository.deleteById(phoneNum);

        if (findEmailDTO.getCode().equals(sms.getCode())) {
            Member member = memberRepository.findByPhone(findEmailDTO.getPhoneNum()).orElseThrow(
                    () -> new UserNotFoundException("전화번호 정보가 존재하지 않습니다.")
            );
            return member.getEmail();
        }
        // 인증코드가 일치하지않는 경우
        throw new NotEqualsCodeException();
    }
}
