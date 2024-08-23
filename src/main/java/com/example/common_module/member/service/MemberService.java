package com.example.common_module.member.service;

import com.example.common_module.member.domain.dto.MemberRequestDTO;
import com.example.common_module.member.domain.dto.MemberResponseDTO;
import com.example.common_module.member.domain.dto.MemberUpdateDTO;

public interface MemberService {

    public MemberResponseDTO findById(Long id);
    public void update(Long id, MemberUpdateDTO requestDto);
    public void delete(Long id);
    public boolean checkDuplicateEmail(String email);
    public void signup(MemberRequestDTO requestDto);
}
