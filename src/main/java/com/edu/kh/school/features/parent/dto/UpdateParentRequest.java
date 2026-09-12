package com.edu.kh.school.features.parent.dto;

import com.edu.kh.school.features.parent.ParentStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record UpdateParentRequest(
        @NotBlank(message = "Full name is required")
        String fullName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        String phone,
        String occupation,
        String address,

        @NotNull(message = "Status is required")
        ParentStatus status,

        Set<UUID> studentIds
) {
}
