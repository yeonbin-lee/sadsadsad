package com.example.domain.member.controller;

import com.example.domain.member.controller.dto.request.fcm.MessagePushServiceRequest;
import com.example.domain.member.service.fcm.FcmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "FCM 알림 전송", description = "FCM 알림 전송에 관한 코드입니다.")
@RequestMapping("/module-common/fcm")
public class FcmController {

    private final FcmService fcmService;

    @PostMapping("/send")
    @Operation(summary = "시스템 알림 전송")
    public ResponseEntity<String> sendNotification(@RequestBody MessagePushServiceRequest request) {
        try {
            // FCM 메시지 전송 요청
            String response = fcmService.sendNotification(request);
            return ResponseEntity.ok("Message sent successfully: " + response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Message failed: " + e.getMessage());
        }
    }
}
