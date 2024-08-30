package com.example.common_module.domain.coolsms.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@Setter
@Getter
@AllArgsConstructor
@Builder
@RedisHash("sms")
//@NoArgsConstructor
public class Sms {

    @Id
    private String id;

    private String code;

    @TimeToLive(unit= TimeUnit.SECONDS)
    private Integer expiration;
}
