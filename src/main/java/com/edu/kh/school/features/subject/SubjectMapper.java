package com.edu.kh.school.features.subject;

import com.edu.kh.school.features.subject.dto.CreateSubjectRequest;
import com.edu.kh.school.features.subject.dto.SubjectResponse;
import com.edu.kh.school.features.subject.dto.SubjectSummaryResponse;
import org.springframework.stereotype.Component;

@Component
public class SubjectMapper {

    public Subject toEntity(CreateSubjectRequest request) {
        return Subject.builder()
                .code(request.code().trim().toUpperCase())
                .name(request.name().trim())
                .description(request.description() != null ? request.description().trim() : null)
                .credit(request.credit())
                .status(SubjectStatus.ACTIVE)
                .build();
    }

    public SubjectResponse toDto(Subject subject) {
        return SubjectResponse.builder()
                .id(subject.getId())
                .code(subject.getCode())
                .name(subject.getName())
                .description(subject.getDescription())
                .credit(subject.getCredit())
                .status(subject.getStatus())
                .createdAt(subject.getCreatedAt())
                .updatedAt(subject.getUpdatedAt())
                .build();
    }

    public SubjectSummaryResponse toSummaryDto(Subject subject) {
        return SubjectSummaryResponse.builder()
                .id(subject.getId())
                .code(subject.getCode())
                .name(subject.getName())
                .credit(subject.getCredit())
                .status(subject.getStatus())
                .build();
    }
}
