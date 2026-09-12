package com.edu.kh.school.features.academic;

import com.edu.kh.school.features.academic.dto.AcademicYearResponse;
import com.edu.kh.school.features.academic.dto.CreateAcademicYearRequest;
import com.edu.kh.school.features.academic.dto.UpdateAcademicYearRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AcademicYearService {
    AcademicYearResponse createAcademicYear(CreateAcademicYearRequest request);

    AcademicYearResponse getAcademicYearById(UUID id);

    AcademicYearResponse getCurrentAcademicYear();

    Page<AcademicYearResponse> getAllAcademicYears(Pageable pageable);

    AcademicYearResponse updateAcademicYear(UUID id, UpdateAcademicYearRequest request);

    AcademicYearResponse activateAcademicYear(UUID id);

    AcademicYearResponse completeAcademicYear(UUID id);
}
