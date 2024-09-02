package com.example.common_module.domain.member.service;

import com.example.common_module.domain.member.controller.dto.request.ProfileDeleteRequest;
import com.example.common_module.domain.member.controller.dto.request.ProfileListRequest;
import com.example.common_module.domain.member.controller.dto.request.ProfileRegisterRequest;
import com.example.common_module.domain.member.controller.dto.request.ProfileUpdateRequest;
import com.example.common_module.domain.member.entity.Member;
import com.example.common_module.domain.member.entity.Profile;
import com.example.common_module.domain.member.repository.MemberRepository;
import com.example.common_module.domain.member.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService{

    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;

    /** 프로필 등록
     * Member 자신의 프로필 내에서 중복되지 않는 닉네임인지 확인
     * */
    @Override
    public void registerProfile(ProfileRegisterRequest request) {

        String email = request.getEmail();
        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 이메일")
        );

        List<Profile> profiles = member.getProfiles();
        if(profiles.stream()
                .anyMatch(profile -> profile.getNickname().equals(request.getNickname()))) {
            throw new DataIntegrityViolationException("중복되는 닉네임입니다.");
        }

        Profile profile = Profile.builder()
                .member(member)
                .nickname(request.getNickname())
                .gender(request.getGender())
                .birthday(request.getBirthday())
                .owner(request.getOwner())
                .pregnancy(request.getPregnancy())
                .smoking(request.getSmoking())
                .hypertension(request.getHypertension())
                .diabetes(request.getDiabetes())
                .build();
        profileRepository.save(profile);
    }

    public Profile updateProfile(ProfileUpdateRequest request) {
        Profile profile = profileRepository.findById(request.getProfile_id()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 프로필")
        );

        profile.setDiabetes(request.getDiabetes());
        profile.setHypertension(request.getHypertension());
        profile.setPregnancy(request.getPregnancy());
        profile.setSmoking(request.getSmoking());

        Profile savedProfile = profileRepository.save(profile);
        return savedProfile;
    }

    public List<Profile> listProfile(ProfileListRequest request){
        Member member = memberRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 이메일")
        );

        List<Profile> profiles = member.getProfiles();
        return profiles;
    }

    public void deleteProfile(ProfileDeleteRequest request){
        Long profile_id = request.getProfile_id();
        profileRepository.deleteById(profile_id);
    }

    public Profile searchProfile(Long profile_id){
        Profile profile = profileRepository.findById(profile_id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 프로필")
        );
        return profile;
    }
}
