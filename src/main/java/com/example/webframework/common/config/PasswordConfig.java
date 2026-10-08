package com.example.webframework.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration // 스프링에 설정 파일(클래스)로 등록
public class PasswordConfig {

    // PasswordEncoder: 비밀번호 해시 생성과 검증 기능을 정의한 인터페이스
    // BCrype

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
