package com.example.common_module.coolsms.model.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@Getter
@Setter
@RedisHash("sms")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Sms {

    @Id
    private String id;

    private String code;

    @TimeToLive(unit= TimeUnit.SECONDS)
    private Integer expiration;
}
