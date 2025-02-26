package com.BUS.DayFrame.controller;

import com.BUS.DayFrame.dto.userRegisterDTO;
import com.BUS.DayFrame.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody userRegisterDTO userDto) {
        userService.registerUser(userDto.getEmail(), userDto.getPassword(), userDto.getName());
        return ResponseEntity.ok("회원가입 성공");
    }

}
