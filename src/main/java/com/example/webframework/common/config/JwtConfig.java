package com.example.webframework.common.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Duration;
import java.util.Base64;

@Configuration
public class JwtConfig {

    @Bean
    public SecretKey jwtKey(@Value("${jwt.secret}") String secret) {
        final byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(secret);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("JWT_SECRET은 Base64 형식이어야 합니다.", exception);
        }

        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT_SECRET 디코딩 결과는 32바이트 이상이어야 합니다.");
        }

        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    // JWT 발급
    @Bean
    public JwtEncoder jwtEncoder(SecretKey jwtKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtKey));
    }

    // JWT 검증
    @Bean
    public JwtDecoder jwtDecoder(SecretKey jwtKey) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(jwtKey).macAlgorithm(MacAlgorithm.HS256).build();

        // 만료 시간 오차 허용하지 않겠다.
        JwtTimestampValidator time = new JwtTimestampValidator(Duration.ZERO);

        JwtIssuerValidator issuer = new JwtIssuerValidator("webframework-server");

        OAuth2TokenValidator<Jwt> requiredClains = jwt -> {
            boolean validSubject;

            try {
                validSubject = jwt.getSubject() != null && Long.parseLong(jwt.getSubject()) > 0;
            } catch (RuntimeException ex) {
                validSubject = false;
            }

            if (jwt.getExpiresAt()== null || !validSubject) {
                return OAuth2TokenValidatorResult.failure(
                        new OAuth2Error("invalid_token", "Invalid JWT token", null)
                );
            }

            return  OAuth2TokenValidatorResult.success();
        };

        decoder.setJwtValidator(
                new DelegatingOAuth2TokenValidator<>(time, issuer, requiredClains)
        );

        return decoder;
    }
}
