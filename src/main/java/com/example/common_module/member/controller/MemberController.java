package com.example.common_module.member.controller;

import com.example.common_module.jwt.JwtTokenProvider;
import com.example.common_module.member.domain.dto.CheckEmailDuplicateDTO;
import com.example.common_module.member.domain.dto.MemberRequestDTO;
import com.example.common_module.member.domain.dto.MemberResponseDTO;
import com.example.common_module.member.domain.dto.MemberUpdateDTO;
import com.example.common_module.member.service.MemberService;
import com.example.common_module.member.service.MemberServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    /** 이메일 중복체크 */
    @PostMapping("/api/v1/user/email/duplicate")
    public ResponseEntity<?> emailDuplicate(@RequestBody @Valid CheckEmailDuplicateDTO requestDto){
        System.out.println("들어온 이메일 =" +requestDto.getEmail());
        Boolean checkEmailDuplicate = memberService.checkDuplicateEmail(requestDto.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(checkEmailDuplicate);
    }

    /** 회원가입 API */
    @PostMapping("/api/v1/auth/signup")
    public ResponseEntity<?> singUp(@RequestBody @Valid MemberRequestDTO requestDto) {
        try{
            this.memberService.signup(requestDto);
            return ResponseEntity.status(HttpStatus.OK).body("User registered successfully!");
        } catch (IllegalStateException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }

    /** 회원정보 조회 API */
    @GetMapping("/api/v1/user")
    public ResponseEntity<?> findUser(@RequestHeader("Authorization") String accessToken) {
        System.out.println("access_token=" + accessToken);
        Long id = this.jwtTokenProvider.getUserIdFromToken(accessToken.substring(7));
        MemberResponseDTO userResponseDto = this.memberService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(userResponseDto);
    }


    /** 회원정보 수정 API */
    @PutMapping("/api/v1/user")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String accessToken,
                                        @RequestBody MemberUpdateDTO requestDto) {
        Long id = this.jwtTokenProvider.getUserIdFromToken(accessToken.substring(7));
        this.memberService.update(id, requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    /** 회원정보 삭제 API */
    @DeleteMapping("/api/v1/user")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String accessToken) {
        Long id = this.jwtTokenProvider.getUserIdFromToken(accessToken.substring(7));
        this.memberService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}