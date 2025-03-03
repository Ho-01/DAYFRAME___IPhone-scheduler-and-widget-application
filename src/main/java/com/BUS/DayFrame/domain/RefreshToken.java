package com.BUS.DayFrame.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "RefreshToken")
public class RefreshToken {

    @Id
    private String email;
    private String token;
    private LocalDateTime expireDate;

    public RefreshToken(String email, String token, LocalDateTime expireDate) {
        this.email = email;
        this.token = token;
        this.expireDate = expireDate;
    }
}
