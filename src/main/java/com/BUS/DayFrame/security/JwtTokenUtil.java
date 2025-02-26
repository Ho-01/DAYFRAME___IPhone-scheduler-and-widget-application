package com.BUS.DayFrame.security;

import com.BUS.DayFrame.domain.RefreshToken;
import com.BUS.DayFrame.repository.RefreshTokenJpaRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class JwtTokenUtil {
    @Autowired
    private RefreshTokenJpaRepository refreshTokenJpaRepository;

    private final SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode("김승호현수민전승현박준서화이팅!끝까지완성시켜보자"));
    private final long ACCESS_TOKEN_EXPIRATION = 1000*60*15; // 15분
    private final long REFRESH_TOKEN_EXPIRATION = 1000*60*60*24*7; // 1주일

    public String generateAccessToken(Long userId){
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+ACCESS_TOKEN_EXPIRATION))
                .signWith(secretKey)
                .compact();
    }

    @Transactional
    public String generateRefreshToken(Long userId){
        String refreshToken = Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+REFRESH_TOKEN_EXPIRATION))
                .signWith(secretKey)
                .compact();
        refreshTokenJpaRepository.deleteById(userId);
        RefreshToken token = new RefreshToken(userId, refreshToken, LocalDateTime.now().plusSeconds(REFRESH_TOKEN_EXPIRATION/1000));
        refreshTokenJpaRepository.save(token);

        return refreshToken;
    }

    private Claims getClaims(String jwtToken){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(jwtToken).getPayload();
    }
    public Long extractUserId(String jwtToken) {
        return Long.parseLong(getClaims(jwtToken).getSubject());
    }
    public void validateToken(String jwtToken){
        if(getClaims(jwtToken).getExpiration().before(new Date())){
            throw new ExpiredJwtException(null, null, "");
        }
    }
}
