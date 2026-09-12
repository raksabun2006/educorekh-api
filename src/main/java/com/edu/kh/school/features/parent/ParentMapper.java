package com.edu.kh.school.features.parent;

import com.edu.kh.school.features.parent.dto.CreateParentRequest;
import com.edu.kh.school.features.parent.dto.ParentResponse;
import com.edu.kh.school.features.parent.dto.ParentSummaryResponse;
import com.edu.kh.school.features.student.StudentMapper;
import com.edu.kh.school.features.student.dto.StudentSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ParentMapper {

    private final StudentMapper studentMapper;

    public Parent toEntity(CreateParentRequest request) {
        return Parent.builder()
                .fullName(request.fullName().trim())
                .email(request.email().trim().toLowerCase())
                .phone(request.phone() != null ? request.phone().trim() : null)
                .occupation(request.occupation() != null ? request.occupation().trim() : null)
                .address(request.address() != null ? request.address().trim() : null)
                .status(ParentStatus.ACTIVE)
                .build();
    }

    public ParentResponse toDto(Parent parent) {
        List<StudentSummaryResponse> childrenList = parent.getChildren() != null
                ? parent.getChildren().stream().map(studentMapper::toSummaryDto).toList()
                : Collections.emptyList();

        return ParentResponse.builder()
                .id(parent.getId())
                .fullName(parent.getFullName())
                .email(parent.getEmail())
                .phone(parent.getPhone())
                .occupation(parent.getOccupation())
                .address(parent.getAddress())
                .status(parent.getStatus())
                .children(childrenList)
                .createdAt(parent.getCreatedAt())
                .updatedAt(parent.getUpdatedAt())
                .build();
    }

    public ParentSummaryResponse toSummaryDto(Parent parent) {
        return ParentSummaryResponse.builder()
                .id(parent.getId())
                .fullName(parent.getFullName())
                .email(parent.getEmail())
                .phone(parent.getPhone())
                .status(parent.getStatus())
                .build();
    }
}
