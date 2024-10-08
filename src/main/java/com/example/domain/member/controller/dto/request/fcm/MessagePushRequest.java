package com.example.domain.member.controller.dto.request.fcm;

import lombok.Getter;

@Getter
public class MessagePushRequest {

    private String targetToken;
    private String title;
    private String body;

}
