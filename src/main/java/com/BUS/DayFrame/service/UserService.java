package com.BUS.DayFrame.service;

import com.BUS.DayFrame.domain.User;
import com.BUS.DayFrame.dto.request.UserCreateDTO;
import com.BUS.DayFrame.dto.request.UserUpdateDTO;
import com.BUS.DayFrame.dto.response.UserResponseDTO;
import com.BUS.DayFrame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional // DB 일관성 유지, 에러 발생 시 롤백할꺼임!
    public User createUser(UserCreateDTO userCreateDTO) {

        // 가입 시 이메일 중복 검사
        Optional<User> existingUser = userRepository.findByEmail(userCreateDTO.getEmail());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userCreateDTO.getPassword());

        User user = User.builder()
                .email(userCreateDTO.getEmail())
                .password(encodedPassword) // 암호화된 패스워드 저장
                .name(userCreateDTO.getName())
                .build();
        return userRepository.save(user);
    }

    @Transactional
    public UserResponseDTO updateUser(Long userId, UserUpdateDTO userUpdateDTO, UserDetails userDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 현재 로그인한 사용자가 요청한 userId와 동일한지 검증
        if (!user.getEmail().equals(userDetails.getUsername())) {
            throw new SecurityException("자신의 계정만 수정할 수 있습니다.");
        }

        // 이름 변경 (null 체크 후 변경)
        if (userUpdateDTO.getName() != null && !userUpdateDTO.getName().isEmpty()) {
            userRepository.updateName(userId, userUpdateDTO.getName());
        }

        // 비밀번호 변경 (null 체크 후 암호화하여 저장)
        if (userUpdateDTO.getPassword() != null && !userUpdateDTO.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(userUpdateDTO.getPassword());
            userRepository.updatePassword(userId, encodedPassword);
        }

        // 변경된 사용자 정보 반환
        User updatedUser = userRepository.findById(userId).orElseThrow();
        return new UserResponseDTO(updatedUser.getEmail(), updatedUser.getName());
    }

    @Transactional
    public void deleteUser(Long userId, UserDetails userDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 현재 로그인한 사용자가 요청한 userId와 동일한지 검증
        if (!user.getEmail().equals(userDetails.getUsername())) {
            throw new SecurityException("자신의 계정만 삭제할 수 있습니다.");
        }

        userRepository.delete(user);
    }
}
