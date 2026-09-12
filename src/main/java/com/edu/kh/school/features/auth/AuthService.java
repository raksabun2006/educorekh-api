package com.edu.kh.school.features.auth;

import com.edu.kh.school.features.auth.dto.CreateUserRequest;
import com.edu.kh.school.features.auth.dto.LoginRequest;
import com.edu.kh.school.features.auth.dto.LoginResponse;
import com.edu.kh.school.features.auth.dto.UserResponse;

public interface AuthService {

    UserResponse createNew(CreateUserRequest request);

    LoginResponse login(LoginRequest request);

    UserResponse getCurrentUser(String email);
}
