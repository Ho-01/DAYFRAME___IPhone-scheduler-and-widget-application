package com.BUS.DayFrame.security.filter;


import com.BUS.DayFrame.security.util.JwtTokenUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { // OncePerRequestFilter 상속

    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String token = resolveToken(request);

        if (token != null) {
            try {
                // ✅ 먼저 validateToken()으로 토큰이 유효한지 확인
                if (!jwtTokenUtil.validateToken(token)) {
                    System.out.println("[에러] JWT 토큰이 유효하지 않음.");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 JWT 토큰입니다.");
                    return;
                }

                // 유효한 토큰이면 이메일 추출
                String email = jwtTokenUtil.getEmailFromToken(token);
                System.out.println("[디버깅] JWT 토큰 검증 성공: " + email);

                UserDetails userDetails = org.springframework.security.core.userdetails.User
                        .withUsername(email)
                        .password("") // 필수 값이므로 빈 문자열 유지
                        .authorities(Collections.singleton(new SimpleGrantedAuthority("USER")))
                        .build();

                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                System.out.println("[디버깅] SecurityContext에 사용자 정보 저장 완료!");
            } catch (JwtException e) {
                System.out.println("[에러] JWT 인증 실패: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 JWT 토큰입니다.");
                return;
            }
        } else {
            System.out.println("[에러] JWT 토큰이 제공되지 않음.");
        }

        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
