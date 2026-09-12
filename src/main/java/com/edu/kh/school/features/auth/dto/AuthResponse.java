package com.edu.kh.school.features.auth.dto;

import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record AuthResponse(

        String accessToken,

        String tokenType,

        UUID userId,

        String username,

        String email,

        Set<String> roles

) {
}
