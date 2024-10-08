package com.example.domain.member.service.fcm;

import com.example.domain.member.controller.dto.request.fcm.MessagePushServiceRequest;
import com.example.domain.member.entity.FCMToken;
import com.example.domain.member.entity.Member;
import com.example.domain.member.repository.FcmRepository;
import com.google.api.core.ApiFuture;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class FcmServiceImpl implements FcmService{

    private final FcmRepository fcmRepository;
    private final FirebaseMessaging firebaseMessaging;

    // 로그인마다 FCMToken 갱신됨
    @Override
    public String updateFCMToken(Member member, String newFcmToken) {

        FCMToken existingFcmToken = fcmRepository.findByMemberId(member.getId());
        // 기존에 존재하는 경우 업데이트
        if (existingFcmToken != null) {
            existingFcmToken.setToken(newFcmToken);
            fcmRepository.save(existingFcmToken);
        } else { // 존재하지 않는 경우 생성
            fcmRepository.save(FCMToken.builder()
                    .member(member)
                    .token(newFcmToken)
                    .createdAt(LocalDate.now())
                    .build()
            );
        }
        // Redis에 저장
        return newFcmToken;
    }

    // 입력받은 FcmToken, title, body를 토대로 FCM에 전송 요청
    public String sendNotification(MessagePushServiceRequest request) {
        try {
            Message message = Message.builder()
                    .setToken(request.targetToken())
                    .setNotification(Notification.builder()
                            .setTitle(request.title())
                            .setBody(request.body())
                            .build())
                    .build();

            // 메시지 전송
            return firebaseMessaging.send(message);
        } catch (Exception e) {
            throw new RuntimeException("FCM 알림 전송에 실패했습니다.", e);
        }
    }


    public ApiFuture<String> sendNotificationAsync(MessagePushServiceRequest request) {
        Message message = Message.builder()
                .setToken(request.targetToken())
                .setNotification(Notification.builder()
                        .setTitle(request.title())
                        .setBody(request.body())
                        .build())
                .build();

        // 비동기 전송
        return firebaseMessaging.sendAsync(message);
    }

}
