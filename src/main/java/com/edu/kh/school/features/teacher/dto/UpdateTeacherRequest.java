package com.edu.kh.school.features.teacher.dto;

import com.edu.kh.school.features.auth.Sex;
import com.edu.kh.school.features.teacher.TeacherStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record UpdateTeacherRequest(
        @NotBlank(message = "Full name is required")
        String fullName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        String phone,

        @NotNull(message = "Sex is required")
        Sex sex,

        @NotNull(message = "Date of birth is required")
        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        String specialization,

        @NotNull(message = "Hire date is required")
        LocalDate hireDate,

        @NotNull(message = "Status is required")
        TeacherStatus status,

        UUID userId
) {
}
