package com.BUS.DayFrame.service;

import com.BUS.DayFrame.domain.RefreshToken;
import com.BUS.DayFrame.domain.User;
import com.BUS.DayFrame.dto.request.LoginRequestDTO;
import com.BUS.DayFrame.dto.request.RefreshTokenRequestDTO;
import com.BUS.DayFrame.dto.response.AccessTokenResponseDTO;
import com.BUS.DayFrame.dto.response.LoginResponseDTO;
import com.BUS.DayFrame.repository.RefreshTokenRepository;
import com.BUS.DayFrame.repository.UserRepository;
import com.BUS.DayFrame.security.util.JwtTokenUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        if (userOptional.isEmpty() || !passwordEncoder.matches(loginRequest.getPassword(), userOptional.get().getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        User user = userOptional.get();
        String accessToken = jwtTokenUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtTokenUtil.generateRefreshToken(user.getEmail());

        refreshTokenRepository.deleteByEmail(user.getEmail()); // 기존 Refresh Token 삭제
        refreshTokenRepository.save(new RefreshToken(user.getEmail(), refreshToken, LocalDateTime.now().plusDays(7)));

        return new LoginResponseDTO(accessToken, refreshToken);
    }

    // Access Token을 사용하여 Refresh Token을 갱신 (수정중)
    @Transactional
    public String refreshRefreshToken(String email) {
        // 기존 Refresh Token 삭제
        refreshTokenRepository.deleteByEmail(email);

        // 새로운 Refresh Token 생성
        String newRefreshToken = jwtTokenUtil.generateRefreshToken(email);

        // 새로운 Refresh Token 저장
        RefreshToken refreshToken = new RefreshToken(email, newRefreshToken, LocalDateTime.now().plusDays(7));
        refreshTokenRepository.save(refreshToken);

        return newRefreshToken;
    }

    // Refresh Token을 사용하여 새로운 Access Token 발급 (수정중)
    @Transactional
    public AccessTokenResponseDTO refreshAccessToken(RefreshTokenRequestDTO refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();

        Optional<RefreshToken> storedToken = refreshTokenRepository.findByToken(refreshToken);
        if (storedToken.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        String email = storedToken.get().getEmail();
        String newAccessToken = jwtTokenUtil.generateAccessToken(email);

        return new AccessTokenResponseDTO(newAccessToken);
    }

    @Transactional
    public void logout(String refreshToken) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Refresh Token입니다."));

        refreshTokenRepository.delete(storedToken); // Refresh Token 삭제
    }
}
