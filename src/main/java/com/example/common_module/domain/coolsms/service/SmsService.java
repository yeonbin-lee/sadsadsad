package com.example.common_module.domain.coolsms.service;

import com.example.common_module.domain.coolsms.controller.dto.request.SmsRequest;
import com.example.common_module.domain.coolsms.controller.dto.request.SmsVerifyRequest;

public interface SmsService {

    public void sendSms(SmsRequest smsRequest);
    public void fakeSendSms(SmsRequest smsRequest);
    public boolean verifySms(SmsVerifyRequest smsVerifyRequest);
}
