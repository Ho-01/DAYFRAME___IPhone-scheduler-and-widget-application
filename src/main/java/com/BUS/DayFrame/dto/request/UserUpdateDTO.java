package com.BUS.DayFrame.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserUpdateDTO {
    private final String password;
    private final String name;
}
