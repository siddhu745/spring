package com.siddhu.spring.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

@Service
public class JWTUtil {

    public static final String SECRET_KEY =
            "foobar_123456789_foobar_123456789_foobar_123456789";

    public String issueToken(String subject) {
        return issueToken(subject, Map.of());
    }
    public String issueToken(String subject, String ...scopes) {
        return issueToken(subject, Map.of("scopes",scopes));
    }
    public String issueToken(String subject, List<String> scopes) {
        return issueToken(subject, Map.of("scopes",scopes));
    }
    public String issueToken(
            String subject,
            Map<String, Object> claims
    ) {
        String token = Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuer("siddhu")
                .issuedAt(Date.from(Instant.now()))
                .expiration(
                        Date.from(
                                Instant.now()
                                        .plus(5, DAYS)
                        )
                )
                .signWith(getSignInKey())
                .compact();

        return token;
    }

    public String getSubject(String token) {
       return getClaims(token).getSubject();
    }

    private Claims getClaims(String token) {
        Claims claims = Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims;
    }

    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public boolean isTokenValid(String jwt, String username) {
        String subject = getSubject(jwt);
        return subject.equals(username) && !isTokenExpired(jwt);
    }

    private boolean isTokenExpired(String jwt) {
        Date today = Date.from(Instant.now());
        return getClaims(jwt).getExpiration().before(today);
    }
}
