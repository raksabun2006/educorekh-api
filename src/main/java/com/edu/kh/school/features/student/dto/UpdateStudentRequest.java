package com.edu.kh.school.features.student.dto;

import com.edu.kh.school.features.auth.Sex;
import com.edu.kh.school.features.student.StudentStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record UpdateStudentRequest(
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

        @NotNull(message = "Status is required")
        StudentStatus status,

        UUID userId,

        UUID classId
) {
}
