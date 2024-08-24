package com.example.common_module.coolsms.service;

import com.example.common_module.coolsms.model.dto.SmsRequestDTO;
import com.example.common_module.coolsms.model.dto.SmsVerifyDTO;

public interface SmsService {

    public void sendSms(SmsRequestDTO smsRequestDto);
    public void fakeSendSms(SmsRequestDTO smsRequestDto);

    public boolean verifySms(SmsVerifyDTO smsVerifyDTO);
}
