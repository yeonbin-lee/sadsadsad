package com.example.common_module.domain.member.controller;

import com.example.common_module.domain.member.controller.dto.request.ProfileDeleteRequest;
import com.example.common_module.domain.member.controller.dto.request.ProfileListRequest;
import com.example.common_module.domain.member.controller.dto.request.ProfileRegisterRequest;
import com.example.common_module.domain.member.controller.dto.request.ProfileUpdateRequest;
import com.example.common_module.domain.member.entity.Profile;
import com.example.common_module.domain.member.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ProfileController {

    private final ProfileService profileService;


    /** 프로필 등록 */
    @PostMapping("/profile/register")
    public ResponseEntity<?> registerProfile(@RequestBody ProfileRegisterRequest request) {
        profileService.registerProfile(request);
        return ResponseEntity.status(HttpStatus.OK).body("Profile registered successfully!");
    }

    /** 프로필 업데이트 */
    @PutMapping("/profile/update")
    public ResponseEntity<?> updateProfile(@RequestBody ProfileUpdateRequest request) {
        Profile profile = profileService.updateProfile(request);
        return ResponseEntity.status(HttpStatus.OK).body(profile);
    }

    /** 프로필 리스트 */
    @GetMapping("/profile/list")
    public ResponseEntity<?> listProfile(@RequestBody ProfileListRequest request) {
        List<Profile> profiles = profileService.listProfile(request);
        return ResponseEntity.status(HttpStatus.OK).body(profiles);
    }

    /** 프로필 삭제 */
    @DeleteMapping("/profile/delete")
    public ResponseEntity<?> deleteProfile(@RequestBody ProfileDeleteRequest request) {
        profileService.deleteProfile(request);
        return ResponseEntity.status(HttpStatus.OK).body("Profile deleted successfully!");
    }

    /** 프로필 조회 */
    @GetMapping("/profile/search/{profile_id}")
    public ResponseEntity<?> searchProfile(@PathVariable Long profile_id) {
        Profile profile = profileService.searchProfile(profile_id);
        return ResponseEntity.status(HttpStatus.OK).body(profile);
    }

}
