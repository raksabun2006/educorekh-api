package com.edu.kh.school.features.auth;

import com.edu.kh.school.features.auth.dto.CreateUserRequest;
import com.edu.kh.school.features.auth.dto.LoginRequest;
import com.edu.kh.school.features.auth.dto.LoginResponse;
import com.edu.kh.school.features.auth.dto.UserResponse;
import com.edu.kh.school.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public UserResponse createNew(CreateUserRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase();

        log.info("Creating new user with email: {}", email);

        if (userRepository.existsByEmail(email)) {
            log.warn("Failed to create user: email already exists");
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already exists"
            );
        }

        User user = mapper.toEntity(request);

        user.setEmail(email);
        user.setPassword(
                passwordEncoder.encode(request.password())
        );

        User savedUser = userRepository.save(user);

        log.info("User created successfully with id: {}", savedUser.getId());

        return mapper.toDto(savedUser);
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase();

        log.info("Login attempt for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid email or password"
                ));

        if (user.getStatus() == UserStatus.SUSPENDED) {
            log.warn("Login failed: account suspended for email {}", email);
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Account is suspended"
            );
        }

        if (user.getStatus() == UserStatus.INACTIVE) {
            log.warn("Login failed: account inactive for email {}", email);
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Account is inactive"
            );
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("Login failed: invalid password for email {}", email);
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }

        String accessToken = jwtService.generateToken(user);

        log.info("User logged in successfully with id: {}", user.getId());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    @Override
    public UserResponse getCurrentUser(String email) {

        String normalizedEmail = email.trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        return mapper.toDto(user);
    }
}
