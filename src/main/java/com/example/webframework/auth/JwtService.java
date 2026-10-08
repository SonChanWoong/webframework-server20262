package com.example.webframework.auth;

import com.example.webframework.user.UserAccount;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {
    private final JwtEncoder jwtEncoder;
    private final long accessTokenExpirationSeconds;

    public JwtService(
            JwtEncoder jwtEncoder,
            @org.springframework.beans.factory.annotation.Value("${jwt.access-token-expiration}") long accessTokenExpirationSeconds
    ) {
        this.jwtEncoder = jwtEncoder;
        this.accessTokenExpirationSeconds = accessTokenExpirationSeconds;
    }

    public String createAccessToken(UserAccount userAccount) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(String.valueOf(userAccount.getId()))
                .issuer("webframework-server")
                .claim("userId", userAccount.getId())
                .claim("nickname", userAccount.getNickname())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(accessTokenExpirationSeconds))
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
    }

    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpirationSeconds;
    }
}
