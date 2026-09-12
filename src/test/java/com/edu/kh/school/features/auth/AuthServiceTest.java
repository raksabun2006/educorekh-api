package com.edu.kh.school.features.auth;

import com.edu.kh.school.features.auth.dto.CreateUserRequest;
import com.edu.kh.school.features.auth.dto.LoginRequest;
import com.edu.kh.school.features.auth.dto.LoginResponse;
import com.edu.kh.school.features.auth.dto.UserResponse;
import com.edu.kh.school.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper = new UserMapper();

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User sampleUser;
    private CreateUserRequest createRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        sampleUser = User.builder()
                .id(userId)
                .fullName("Bun Raksa")
                .email("raksa@example.com")
                .password("encoded_password_123")
                .sex(Sex.MALE)
                .role(Role.STUDENT)
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createRequest = CreateUserRequest.builder()
                .fullName("Bun Raksa")
                .email("  Raksa@Example.com ")
                .password("Password123")
                .sex(Sex.MALE)
                .build();

        loginRequest = LoginRequest.builder()
                .email("  Raksa@Example.com ")
                .password("Password123")
                .build();
    }

    @Test
    @DisplayName("Should successfully register a new user with hashed password and default role/status")
    void createNew_Success() {
        when(userRepository.existsByEmail("raksa@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123")).thenReturn("encoded_password_123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(sampleUser.getId());
            user.setCreatedAt(sampleUser.getCreatedAt());
            return user;
        });

        UserResponse response = authService.createNew(createRequest);

        assertNotNull(response);
        assertEquals("raksa@example.com", response.email());
        assertEquals("Bun Raksa", response.fullName());
        assertEquals(Role.STUDENT, response.role());
        assertEquals(UserStatus.ACTIVE, response.status());
        assertEquals(Sex.MALE, response.sex());

        verify(userRepository).save(argThat(user ->
                user.getEmail().equals("raksa@example.com") &&
                user.getPassword().equals("encoded_password_123") &&
                user.getRole() == Role.STUDENT &&
                user.getStatus() == UserStatus.ACTIVE
        ));
    }

    @Test
    @DisplayName("Should throw 409 CONFLICT when email already exists")
    void createNew_DuplicateEmail_ThrowsConflict() {
        when(userRepository.existsByEmail("raksa@example.com")).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                authService.createNew(createRequest)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Email already exists", exception.getReason());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should successfully login active user and return JWT token")
    void login_Success() {
        when(userRepository.findByEmail("raksa@example.com")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("Password123", "encoded_password_123")).thenReturn(true);
        when(jwtService.generateToken(sampleUser)).thenReturn("jwt.token.value");

        LoginResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwt.token.value", response.accessToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals("raksa@example.com", response.email());
        assertEquals(Role.STUDENT, response.role());
        assertEquals(sampleUser.getId(), response.userId());
    }

    @Test
    @DisplayName("Should throw 401 UNAUTHORIZED when login email does not exist")
    void login_EmailNotFound_ThrowsUnauthorized() {
        when(userRepository.findByEmail("raksa@example.com")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                authService.login(loginRequest)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Invalid email or password", exception.getReason());
    }

    @Test
    @DisplayName("Should throw 401 UNAUTHORIZED when login password does not match")
    void login_InvalidPassword_ThrowsUnauthorized() {
        when(userRepository.findByEmail("raksa@example.com")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("Password123", "encoded_password_123")).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                authService.login(loginRequest)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Invalid email or password", exception.getReason());
    }

    @Test
    @DisplayName("Should throw 403 FORBIDDEN when user is SUSPENDED")
    void login_SuspendedUser_ThrowsForbidden() {
        sampleUser.setStatus(UserStatus.SUSPENDED);
        when(userRepository.findByEmail("raksa@example.com")).thenReturn(Optional.of(sampleUser));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                authService.login(loginRequest)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("Account is suspended", exception.getReason());
    }

    @Test
    @DisplayName("Should throw 403 FORBIDDEN when user is INACTIVE")
    void login_InactiveUser_ThrowsForbidden() {
        sampleUser.setStatus(UserStatus.INACTIVE);
        when(userRepository.findByEmail("raksa@example.com")).thenReturn(Optional.of(sampleUser));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                authService.login(loginRequest)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("Account is inactive", exception.getReason());
    }

    @Test
    @DisplayName("Should return user details for current authenticated user")
    void getCurrentUser_Success() {
        when(userRepository.findByEmail("raksa@example.com")).thenReturn(Optional.of(sampleUser));

        UserResponse response = authService.getCurrentUser("  Raksa@example.com ");

        assertNotNull(response);
        assertEquals(sampleUser.getId(), response.id());
        assertEquals("Bun Raksa", response.fullName());
        assertEquals("raksa@example.com", response.email());
        assertEquals(Role.STUDENT, response.role());
        assertEquals(UserStatus.ACTIVE, response.status());
    }

    @Test
    @DisplayName("Should throw 404 NOT_FOUND when current user email is not in database")
    void getCurrentUser_NotFound_ThrowsNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                authService.getCurrentUser("unknown@example.com")
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("User not found", exception.getReason());
    }
}
