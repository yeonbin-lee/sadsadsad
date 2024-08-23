package com.example.common_module.coolsms.controller;

import com.example.common_module.coolsms.service.SmsService;
import com.example.common_module.coolsms.model.dto.SmsRequestDTO;
import com.example.common_module.coolsms.model.dto.SmsVerifyDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
public class SmsController {

    private final SmsService smsService;

    public SmsController(SmsService smsService) {
        this.smsService = smsService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendSMS(@RequestBody @Valid SmsRequestDTO smsRequestDto){
        smsService.sendSms(smsRequestDto);
        return ResponseEntity.ok("문자를 전송했습니다.");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifySMS(@RequestBody @Valid SmsVerifyDTO smsVerifyDTO){
        return new ResponseEntity<>(smsService.verifySms(smsVerifyDTO), HttpStatus.OK);
    }

}