package com.edu.kh.school.features.auth.dto;

import com.edu.kh.school.features.auth.Role;
import com.edu.kh.school.features.auth.Sex;
import com.edu.kh.school.features.auth.UserStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UserResponse(
        UUID id,
        String fullName,
        String email,
        Sex sex,
        Role role,
        UserStatus status,
        LocalDateTime createdAt
) {
}
