package com.example.common_module.coolsms.service;

import com.example.common_module.coolsms.utils.SmsCertificationUtil;
import com.example.common_module.coolsms.repository.SmsRepository;
import com.example.common_module.coolsms.model.entity.Sms;
import com.example.common_module.coolsms.model.dto.SmsRequestDTO;
import com.example.common_module.coolsms.model.dto.SmsVerifyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {

    private final SmsCertificationUtil smsCertificationUtil;
    private final SmsRepository smsRepository;

    //의존성 주입
    public SmsServiceImpl(@Autowired SmsCertificationUtil smsCertificationUtil, SmsRepository smsRepository) {
        this.smsCertificationUtil = smsCertificationUtil;
        this.smsRepository = smsRepository;
    }

    @Override // SmsService 인터페이스 메서드 구현
    public void sendSms(SmsRequestDTO smsRequestDto) {
        String phoneNum = smsRequestDto.getPhoneNum(); // SmsrequestDto에서 전화번호를 가져온다.

        String certificationCode = Integer.toString((int)(Math.random() * (999999 - 100000 + 1)) + 100000); // 6자리 인증 코드를 랜덤으로 생성
        smsCertificationUtil.sendSMS(phoneNum, String.valueOf(certificationCode)); // SMS 인증 유틸리티를 사용하여 SMS 발송

        // 이미 인증코드를 발급받았다면 해당 전화번호의 인증코드를 삭제한다.
        if (smsRepository.existsById(phoneNum)){
            smsRepository.deleteById(phoneNum);
        }

        // 인증코드 저장
        smsRepository.save(
                Sms.builder()
                        .id(phoneNum)
                        .code(certificationCode)
                        .expiration(180) // 180 = 60 * 3 (5분)
                        .build()
        );
    }

    @Override
    public boolean verifySms(SmsVerifyDTO smsVerifyDTO){
        Sms sms = smsRepository.findById(smsVerifyDTO.getPhoneNum()).get();
        if(sms.getCode().equals(smsVerifyDTO.getCertificationCode())){
            smsRepository.deleteById(smsVerifyDTO.getPhoneNum());
            return true;
        }
        return false;
    }
}