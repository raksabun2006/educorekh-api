package com.edu.kh.school.features.auth.dto;

import com.edu.kh.school.features.auth.Role;
import lombok.Builder;

import java.util.UUID;

@Builder
public record LoginResponse(
        String accessToken,
        String tokenType,
        UUID userId,
        String email,
        Role role
) {
}
