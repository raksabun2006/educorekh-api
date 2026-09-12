package com.edu.kh.school.features.auth;

import com.edu.kh.school.features.auth.dto.CreateUserRequest;
import com.edu.kh.school.features.auth.dto.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(CreateUserRequest request) {
        return User.builder()
                .fullName(request.fullName().trim())
                .email(request.email().trim().toLowerCase())
                .sex(request.sex())
                .role(Role.STUDENT)
                .status(UserStatus.ACTIVE)
                .build();
    }

    public UserResponse toDto(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .sex(user.getSex())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
