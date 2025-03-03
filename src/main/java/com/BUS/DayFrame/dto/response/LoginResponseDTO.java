package com.BUS.DayFrame.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 로그인 성공 시 JWT 액세스 토큰과 리프레시 토큰을 반환할 DTO

@Getter
@AllArgsConstructor
public class LoginResponseDTO {
    private String accessToken;
    private String refreshToken;
}
