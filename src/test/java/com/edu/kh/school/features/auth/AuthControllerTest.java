package com.edu.kh.school.features.auth;

import com.edu.kh.school.features.auth.dto.CreateUserRequest;
import com.edu.kh.school.features.auth.dto.LoginRequest;
import com.edu.kh.school.features.auth.dto.LoginResponse;
import com.edu.kh.school.features.auth.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    @DisplayName("POST /api/v1/auth/register should return 201 Created and UserResponse without password")
    void registerUser_Success() throws Exception {
        UserResponse response = UserResponse.builder()
                .id(UUID.randomUUID())
                .fullName("Bun Raksa")
                .email("raksa@example.com")
                .sex(Sex.MALE)
                .role(Role.STUDENT)
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        when(authService.createNew(any(CreateUserRequest.class))).thenReturn(response);

        String jsonPayload = """
                {
                    "fullName": "Bun Raksa",
                    "email": "raksa@example.com",
                    "password": "Password123",
                    "sex": "MALE"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("Bun Raksa"))
                .andExpect(jsonPath("$.email").value("raksa@example.com"))
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("POST /api/v1/auth/login should return 200 OK and LoginResponse with JWT token")
    void loginUser_Success() throws Exception {
        UUID userId = UUID.randomUUID();
        LoginResponse response = LoginResponse.builder()
                .accessToken("mocked.jwt.token")
                .tokenType("Bearer")
                .userId(userId)
                .email("raksa@example.com")
                .role(Role.STUDENT)
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        String jsonPayload = """
                {
                    "email": "raksa@example.com",
                    "password": "Password123"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mocked.jwt.token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.email").value("raksa@example.com"))
                .andExpect(jsonPath("$.role").value("STUDENT"));
    }

    @Test
    @DisplayName("GET /api/v1/auth/me should return current user info when authenticated")
    void getCurrentUser_Authenticated() throws Exception {
        UserResponse response = UserResponse.builder()
                .id(UUID.randomUUID())
                .fullName("Bun Raksa")
                .email("raksa@example.com")
                .sex(Sex.MALE)
                .role(Role.STUDENT)
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        when(authService.getCurrentUser("raksa@example.com")).thenReturn(response);

        Authentication auth = new UsernamePasswordAuthenticationToken("raksa@example.com", null);

        mockMvc.perform(get("/api/v1/auth/me").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Bun Raksa"))
                .andExpect(jsonPath("$.email").value("raksa@example.com"))
                .andExpect(jsonPath("$.role").value("STUDENT"));
    }
}
