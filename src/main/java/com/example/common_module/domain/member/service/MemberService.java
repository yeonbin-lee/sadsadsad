package com.example.common_module.domain.member.service;

import com.example.common_module.domain.member.controller.dto.request.*;
import com.example.common_module.domain.member.controller.dto.response.MemberResponse;

public interface MemberService {

    public MemberResponse findById(String accessToken);
//    public void update(Long id, MemberUpdateDTO requestDto);
    public void delete(String accessToken);
    public boolean checkDuplicateEmail(String email);
    public String findEmailByPhone(FindEmailByPhoneRequest findEmailByPhoneRequest);
    public void changePasswordV1(PwFindRequest pwFindRequest);
    public void changePasswordV2(String accessToken, PwChangeRequest pwChangeRequest);
    public void changeNickname(String accessToken, NicknameChangeRequest nicknameChangeRequest);

}
