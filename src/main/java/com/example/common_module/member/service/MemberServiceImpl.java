package com.example.common_module.member.service;

import com.example.common_module.member.domain.dto.MemberRequestDTO;
import com.example.common_module.member.domain.entity.Member;
import com.example.common_module.member.domain.dto.MemberResponseDTO;
import com.example.common_module.member.domain.dto.MemberUpdateDTO;
import com.example.common_module.member.domain.entity.Role;
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

}
