package com.example.webframework.auth;

import com.example.webframework.user.UserAccount;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;

@Service
public class JwtService {
    private final SecretKey signingKey;
    private final JwtEncoder jwtEncoder;
    private final long accessTokenExpirationSeconds;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            JwtEncoder jwtEncoder,
            @Value("${jwt.access-token-expiration}") long accessTokenExpirationSeconds
    ) {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.jwtEncoder = jwtEncoder;
        this.accessTokenExpirationSeconds = accessTokenExpirationSeconds;
    }

    public String createAccessToken(UserAccount userAccount) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(userAccount.getEmail())
                .claim("userId", userAccount.getId())
                .claim("nickname", userAccount.getNickname())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(accessTokenExpirationSeconds))
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
    }

    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpirationSeconds;
    }
}
