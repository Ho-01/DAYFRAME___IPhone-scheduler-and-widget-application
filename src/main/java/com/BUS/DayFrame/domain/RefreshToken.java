package com.BUS.DayFrame.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Token")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "token")
    private String refreshToken;

    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;


    public RefreshToken() {}

    public RefreshToken(Long userId, String refreshToken, LocalDateTime expirationTime) {
        this.userId = userId;
        this.refreshToken = refreshToken;
        this.expirationTime = expirationTime;
    }

    // GETTER
    // SETTER
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public LocalDateTime getExpirationTime() { return expirationTime; }
    public void setExpirationTime(LocalDateTime expirationTime) { this.expirationTime = expirationTime; }
}
