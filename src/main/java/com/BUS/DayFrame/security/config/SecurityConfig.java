package com.BUS.DayFrame.security.config;

import com.BUS.DayFrame.security.filter.JwtAuthenticationFilter;
import com.BUS.DayFrame.security.util.JwtTokenUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtTokenUtil jwtTokenUtil;

    public SecurityConfig(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())  // CSRF 비활성화 (JWT 사용 시 필요)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**", "/auth/login", "/users/register").permitAll()  // 인증 없이 접근 가능
                        .anyRequest().authenticated()  // 나머지 요청은 인증 필요
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT 사용 → 세션 X
                .headers(headers -> headers.frameOptions().disable())  // H2 콘솔을 위해 iframe 허용
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenUtil), UsernamePasswordAuthenticationFilter.class) // JWT 필터 적용
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
