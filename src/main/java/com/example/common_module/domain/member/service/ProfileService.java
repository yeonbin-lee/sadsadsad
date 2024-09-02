package com.example.common_module.domain.member.service;

import com.example.common_module.domain.member.controller.dto.request.ProfileDeleteRequest;
import com.example.common_module.domain.member.controller.dto.request.ProfileListRequest;
import com.example.common_module.domain.member.controller.dto.request.ProfileRegisterRequest;
import com.example.common_module.domain.member.controller.dto.request.ProfileUpdateRequest;
import com.example.common_module.domain.member.entity.Profile;

import java.util.List;

public interface ProfileService {

    // 프로필 등록
    public void registerProfile(ProfileRegisterRequest request);

    // 프로필 업데이트
    public Profile updateProfile(ProfileUpdateRequest request);

    // 프로필 리스트
    public List<Profile> listProfile(ProfileListRequest request);

    // 프로필 삭제
    public void deleteProfile(ProfileDeleteRequest request);

    // 프로필 조회
    public Profile searchProfile(Long profile_id);
}
