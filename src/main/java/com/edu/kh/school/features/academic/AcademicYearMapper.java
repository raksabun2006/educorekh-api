package com.edu.kh.school.features.academic;

import com.edu.kh.school.features.academic.dto.AcademicYearResponse;
import com.edu.kh.school.features.academic.dto.CreateAcademicYearRequest;
import org.springframework.stereotype.Component;

@Component
public class AcademicYearMapper {

    public AcademicYear toEntity(CreateAcademicYearRequest request) {
        return AcademicYear.builder()
                .name(request.name().trim())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .status(AcademicYearStatus.UPCOMING)
                .build();
    }

    public AcademicYearResponse toDto(AcademicYear entity) {
        return AcademicYearResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
