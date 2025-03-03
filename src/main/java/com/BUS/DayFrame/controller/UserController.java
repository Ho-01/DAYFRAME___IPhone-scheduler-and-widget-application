package com.BUS.DayFrame.controller;

import com.BUS.DayFrame.domain.User;
import com.BUS.DayFrame.dto.request.UserCreateDTO;
import com.BUS.DayFrame.dto.request.UserUpdateDTO;
import com.BUS.DayFrame.dto.response.UserResponseDTO;
import com.BUS.DayFrame.repository.UserRepository;
import com.BUS.DayFrame.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserCreateDTO userCreateDTO) {
        try {
            User createdUser = userService.createUser(userCreateDTO);
            return ResponseEntity.ok(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());  // 중복 가입 예외 처리
        }
    }

    // 현재 로그인한 사용자 정보 조회
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        UserResponseDTO response = new UserResponseDTO(user.getEmail(), user.getName());
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response
        ));
    }

    @PutMapping("/{user_id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long user_id,
            @RequestBody UserUpdateDTO userUpdateDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        UserResponseDTO updatedUser = userService.updateUser(user_id, userUpdateDTO, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{user_id}")
    public ResponseEntity<Map<String, String>> deleteUser(
            @PathVariable Long user_id,
            @AuthenticationPrincipal UserDetails userDetails) {

        userService.deleteUser(user_id, userDetails);
        return ResponseEntity.ok(Map.of("message", "회원 탈퇴 성공"));
    }
}
