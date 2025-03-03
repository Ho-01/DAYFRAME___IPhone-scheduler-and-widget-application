package com.BUS.DayFrame.repository;

import com.BUS.DayFrame.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String token); // 리프레시 토큰으로 조회
    void deleteByEmail(String email);
}
