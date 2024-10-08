package com.example.domain.member.controller;

import com.example.domain.member.controller.dto.request.profile.ProfileDeleteRequest;
import com.example.domain.member.controller.dto.request.profile.ProfileListRequest;
import com.example.domain.member.controller.dto.request.profile.ProfileRegisterRequest;
import com.example.domain.member.controller.dto.request.profile.ProfileUpdateRequest;
import com.example.domain.member.controller.dto.response.ProfileDTO;
import com.example.domain.member.controller.dto.response.ProfileResponse;
import com.example.domain.member.service.profileService.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "프로필 API", description = "프로필에 관한 코드입니다.")
@RequestMapping("/module-common/profile")
public class ProfileController {

    private final ProfileService profileService;

    /**
     * 프로필 등록 API
     * - 프로필 내 중복 닉네임 불가능
     * - 기본 프로필은 최대 1개 등록 가능
     * - (기본 + 추가) 프로필 최대 10개 가능
     * @param request - email
     * @param request - nickname, gender, owner, birthday, pregnancy, smoking, hypertension, diabetes 사용자에게 받을거
     * */
    @PostMapping("/register")
    @Operation(summary = "프로필 등록", description = "메인(기본) 프로필은 등록 못함, 무조건 추가 프로필임")
    public ResponseEntity<?> registerProfile(@RequestBody ProfileRegisterRequest request) {
        profileService.registerProfile(request);
        return ResponseEntity.status(HttpStatus.OK).body("Profile registered successfully!");
    }


    /**
     * 프로필 업데이트 API - 당뇨병, 고혈압, 임신, 담배 유무 변경 가능
     * @param request - profileId
     * @param request - pregnancy, smoking, hypertension, diabetes 사용자 입력
     * */
    @PutMapping("/update")
    @Operation(summary = "프로필 업데이트")
    public ResponseEntity<?> updateProfile(@RequestBody ProfileUpdateRequest request) {
        profileService.updateProfile(request);
        return ResponseEntity.status(HttpStatus.OK).body("profile updated successfully!");
    }

    /**
     * 프로필 리스트 API
     * @param request - email
     * @return ProfileDTO(profileId, nickname) 리스트
     * */
    @GetMapping("/list")
    @Operation(summary = "입력받은 email의 프로필 리스트 조회", description = "프로필id, 프로필 닉네임 리턴")
    public ResponseEntity<?> listProfile(@RequestBody ProfileListRequest request) {
        List<ProfileDTO> profiles = profileService.listProfile(request);
        return ResponseEntity.status(HttpStatus.OK).body(profiles);
    }

    /**
     * 프로필 삭제 API
     * @param request - profileId
     * 기본 프로필 삭제 불가, 추가 프로필만 삭제 가능
     * */
    @DeleteMapping("/delete")
    @Operation(summary = "프로필 삭제", description = "기본(메인) 프로필은 삭제 불가능")
    public ResponseEntity<?> deleteProfile(@RequestBody ProfileDeleteRequest request) {
        profileService.deleteProfile(request);
        return ResponseEntity.status(HttpStatus.OK).body("Profile deleted successfully!");
    }

    /**
     * 프로필 조회 API
     * @param profileId
     * */
    @GetMapping("/search/{profileId}")
    @Operation(summary = "프로필id로 프로필 조회")
    public ResponseEntity<?> searchProfile(@PathVariable("profileId") Long profileId) {
        ProfileResponse profileResponse = profileService.searchProfile(profileId);
        return ResponseEntity.status(HttpStatus.OK).body(profileResponse);
    }

}
