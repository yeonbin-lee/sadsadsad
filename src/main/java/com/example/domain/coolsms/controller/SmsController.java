package com.example.domain.coolsms.controller;

import com.example.domain.coolsms.controller.dto.request.SmsRequest;
import com.example.domain.coolsms.controller.dto.request.SmsVerifyRequest;
import com.example.domain.coolsms.service.SmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "SMS API", description = "인증코드 전송에 관한 코드입니다.")
@RequestMapping("/module-common/sms")
public class SmsController {

    private final SmsService smsService;


    /**
     * 인증번호 전송 API
     * @param request - phone 사용자 입력
     */
    @PostMapping("/send")
    @Operation(summary = "CoolSms 인증번호 전송", description = "이 메소드 사용 시 건당 비용 나옴 -> /fake/send 사용바람")
    public ResponseEntity<?> sendSMS(@RequestBody SmsRequest request){
        String code = smsService.sendSms(request);
        return ResponseEntity.ok(code);
    }

    /** 비용 문제로 만든 Redis 저장 인증코드*/
    @PostMapping("/fake/send")
    @Operation(summary = "[가짜] CoolSms 인증번호 전송")
    public ResponseEntity<?> fakeSendSMS(@RequestBody SmsRequest request){
        String code = smsService.fakeSendSms(request);
        return ResponseEntity.ok(code);
    }

    /**
     * 인증코드 확인
     * @param request - phone
     * @param request - code 사용자 입력
     * @return boolean (true - 일치, false - 불일치)
     */
    @PostMapping("/verify")
    @Operation(summary = "CoolSms 인증번호 인증")
    public ResponseEntity<?> verifySMS(@RequestBody SmsVerifyRequest request){
        return new ResponseEntity<>(smsService.verifySms(request), HttpStatus.OK);
    }

}