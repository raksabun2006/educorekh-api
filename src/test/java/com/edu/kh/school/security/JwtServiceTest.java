package com.edu.kh.school.security;

import com.edu.kh.school.features.auth.Role;
import com.edu.kh.school.features.auth.Sex;
import com.edu.kh.school.features.auth.User;
import com.edu.kh.school.features.auth.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("my-super-secret-key-that-is-at-least-256-bits-long-1234567890", 3600000);
        testUser = User.builder()
                .id(UUID.randomUUID())
                .fullName("Bun Raksa")
                .email("raksa@example.com")
                .password("encoded_pass")
                .sex(Sex.MALE)
                .role(Role.STUDENT)
                .status(UserStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Should generate token with claims and extract username")
    void generateAndValidateToken() {
        String token = jwtService.generateToken(testUser);

        assertNotNull(token);
        assertEquals("raksa@example.com", jwtService.extractUsername(token));
        assertTrue(jwtService.isTokenValid(token));

        org.springframework.security.core.userdetails.User userDetails =
                new org.springframework.security.core.userdetails.User(
                        "raksa@example.com",
                        "password",
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"))
                );

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }
}
