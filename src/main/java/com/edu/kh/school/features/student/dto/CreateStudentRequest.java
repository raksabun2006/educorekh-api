package com.edu.kh.school.features.student.dto;

import com.edu.kh.school.features.auth.Sex;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record CreateStudentRequest(
        @NotBlank(message = "Student code is required")
        String studentCode,

        @NotBlank(message = "Full name is required")
        String fullName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        String phone,

        @NotNull(message = "Date of birth is required")
        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @NotNull(message = "Sex is required")
        Sex sex,

        String address,

        @NotNull(message = "Admission date is required")
        LocalDate admissionDate,

        UUID userId,

        UUID classId
) {
}
