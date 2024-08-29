package com.example.common_module.domain.member.controller;

import com.example.common_module.domain.member.controller.dto.request.*;
import com.example.common_module.domain.member.controller.dto.response.MemberResponse;
import com.example.common_module.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;


    /** 이메일 중복체크 */
    @PostMapping("/api/v1/user/email/duplicate")
    public ResponseEntity<?> emailDuplicate(@RequestBody @Valid CheckEmailDuplicateRequest requestDto){
        System.out.println("들어온 이메일 =" +requestDto.getEmail());
        Boolean checkEmailDuplicate = memberService.checkDuplicateEmail(requestDto.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(checkEmailDuplicate);
    }


//    /** 전화번호로 이메일 찾기 */
//    @PostMapping("/find/email")
//    public ResponseEntity<?> findEmailByPhone(@RequestBody @Valid FindEmailDTO findEmailDTO){
//        try{
//            String email = memberService.findEmailByPhone(findEmailDTO);
//            return ResponseEntity.status(HttpStatus.OK).body(email);
//        } catch (IllegalStateException e){
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

    /** 1. 비밀번호 재설정
     *  - 비밀번호를 잊어버렸을 경우
     * */
    @PutMapping("/change/password/v1")
    public ResponseEntity<?> changePasswordV1(@RequestBody @Valid PwFindRequest pwFindRequest){
        try{
            memberService.changePasswordV1(pwFindRequest);
            return ResponseEntity.status(HttpStatus.OK).body("Password changed successfully!");
        } catch (IllegalStateException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /** 2. 비밀번호 재설정
     *  - 비밀번호를 변경하고싶을 경우
     * */
    @PutMapping("/change/password/v2")
    public ResponseEntity<?> changePasswordV2(@RequestHeader("Authorization") String accessToken, @RequestBody @Valid PwChangeRequest pwChangeRequest){
        System.out.println("access_token=" + accessToken);
        memberService.changePasswordV2(accessToken, pwChangeRequest);
        return ResponseEntity.status(HttpStatus.OK).body("Password changed successfully!");

    }

    /** 회원 닉네임 수정 */
    @PutMapping("/change/nickname")
    public ResponseEntity<?> changeNickname(@RequestHeader("Authorization") String accessToken, @RequestBody @Valid NicknameChangeRequest nicknameChangeRequest) {
        try{
            memberService.changeNickname(accessToken, nicknameChangeRequest);
            return ResponseEntity.status(HttpStatus.OK).body("Nickname changed successfully!");
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /** 회원정보 조회 API */
    @GetMapping("/api/v1/user")
    public ResponseEntity<?> findUser(@RequestHeader("Authorization") String accessToken) {
        System.out.println("access_token=" + accessToken);
        MemberResponse userResponseDto = memberService.findById(accessToken);
        return ResponseEntity.status(HttpStatus.OK).body(userResponseDto);
    }

    /** 회원정보 삭제 API */
    @DeleteMapping("/api/v1/user")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String accessToken) {
        memberService.delete(accessToken);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}