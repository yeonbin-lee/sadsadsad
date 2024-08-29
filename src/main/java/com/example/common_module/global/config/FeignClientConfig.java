package com.example.common_module.global.config;

import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    public static class CustomErrorDecoder implements ErrorDecoder {

        @Override
        public Exception decode(String methodKey, Response response) {
            if (response.status() == 401) {
                // Unauthorized (401) 에러 처리
                return new IllegalAccessException("예외처리 필요");
//                return new CustomException(ErrorCode.UNAUTHORIZED_TOKEN);
            } else if (response.status() == 404) {
                // Not Found (404) 에러 처리s
                return new IllegalAccessException("예외처리 필요");
//                return new CustomException(ErrorCode.NOT_FOUND);
            }

            // 기본적으로는 FeignException을 던집니다.
            return FeignException.errorStatus(methodKey, response);
        }
    }
}
