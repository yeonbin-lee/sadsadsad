package com.example.domain.member.service.fcm;

import com.example.domain.member.controller.dto.request.fcm.MessagePushServiceRequest;
import com.example.domain.member.entity.Member;
import com.google.api.core.ApiFuture;

public interface FcmService {

//    public void updateFCMToken(Member member, String newFcmToken);
    public String updateFCMToken(Member member, String newFcmToken);
    public String sendNotification(MessagePushServiceRequest request);
    public ApiFuture<String> sendNotificationAsync(MessagePushServiceRequest request);
}
