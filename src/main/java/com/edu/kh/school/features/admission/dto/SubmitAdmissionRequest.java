package com.edu.kh.school.features.admission.dto;

import com.edu.kh.school.features.auth.Sex;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record SubmitAdmissionRequest(
        @NotBlank(message = "Applicant full name is required")
        String applicantFullName,

        @NotBlank(message = "Applicant email is required")
        @Email(message = "Invalid email format")
        String applicantEmail,

        String applicantPhone,

        @NotNull(message = "Date of birth is required")
        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @NotNull(message = "Sex is required")
        Sex sex,

        String address,

        String previousSchool,

        @NotBlank(message = "Applying for grade is required (e.g. 10)")
        String applyingForGrade,

        @NotNull(message = "Academic year ID is required")
        UUID academicYearId
) {
}
