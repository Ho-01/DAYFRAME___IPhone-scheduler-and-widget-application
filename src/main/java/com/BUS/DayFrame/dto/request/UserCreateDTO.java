package com.BUS.DayFrame.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserCreateDTO {
    private final String email;
    private final String password;
    private final String name;
}
