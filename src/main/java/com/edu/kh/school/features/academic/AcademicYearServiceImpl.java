package com.edu.kh.school.features.academic;

import com.edu.kh.school.exception.BusinessException;
import com.edu.kh.school.exception.DuplicateResourceException;
import com.edu.kh.school.exception.ResourceNotFoundException;
import com.edu.kh.school.features.academic.dto.AcademicYearResponse;
import com.edu.kh.school.features.academic.dto.CreateAcademicYearRequest;
import com.edu.kh.school.features.academic.dto.UpdateAcademicYearRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AcademicYearServiceImpl implements AcademicYearService {

    private final AcademicYearRepository academicYearRepository;
    private final AcademicYearMapper mapper;

    @Override
    @Transactional
    public AcademicYearResponse createAcademicYear(CreateAcademicYearRequest request) {
        String name = request.name().trim();

        if (academicYearRepository.existsByName(name)) {
            throw new DuplicateResourceException("Academic year already exists with name: " + name);
        }

        if (request.startDate().isAfter(request.endDate())) {
            throw new BusinessException("Start date must be before end date");
        }

        AcademicYear academicYear = mapper.toEntity(request);
        AcademicYear saved = academicYearRepository.save(academicYear);
        log.info("Created academic year with id: {}", saved.getId());

        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AcademicYearResponse getAcademicYearById(UUID id) {
        AcademicYear year = academicYearRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found with id: " + id));
        return mapper.toDto(year);
    }

    @Override
    @Transactional(readOnly = true)
    public AcademicYearResponse getCurrentAcademicYear() {
        AcademicYear year = academicYearRepository.findByStatus(AcademicYearStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("No active academic year found"));
        return mapper.toDto(year);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AcademicYearResponse> getAllAcademicYears(Pageable pageable) {
        return academicYearRepository.findAll(pageable)
                .map(mapper::toDto);
    }

    @Override
    @Transactional
    public AcademicYearResponse updateAcademicYear(UUID id, UpdateAcademicYearRequest request) {
        AcademicYear year = academicYearRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found with id: " + id));

        String name = request.name().trim();
        if (!year.getName().equalsIgnoreCase(name) && academicYearRepository.existsByName(name)) {
            throw new DuplicateResourceException("Academic year already exists with name: " + name);
        }

        if (request.startDate().isAfter(request.endDate())) {
            throw new BusinessException("Start date must be before end date");
        }

        year.setName(name);
        year.setStartDate(request.startDate());
        year.setEndDate(request.endDate());
        year.setStatus(request.status());

        AcademicYear saved = academicYearRepository.save(year);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public AcademicYearResponse activateAcademicYear(UUID id) {
        AcademicYear yearToActivate = academicYearRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found with id: " + id));

        // Ensure only one is ACTIVE at a time
        List<AcademicYear> activeYears = academicYearRepository.findAllByStatus(AcademicYearStatus.ACTIVE);
        for (AcademicYear active : activeYears) {
            if (!active.getId().equals(id)) {
                active.setStatus(AcademicYearStatus.COMPLETED);
                academicYearRepository.save(active);
            }
        }

        yearToActivate.setStatus(AcademicYearStatus.ACTIVE);
        AcademicYear saved = academicYearRepository.save(yearToActivate);
        log.info("Activated academic year: {}", saved.getName());

        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public AcademicYearResponse completeAcademicYear(UUID id) {
        AcademicYear year = academicYearRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found with id: " + id));

        year.setStatus(AcademicYearStatus.COMPLETED);
        AcademicYear saved = academicYearRepository.save(year);
        log.info("Completed academic year: {}", saved.getName());

        return mapper.toDto(saved);
    }
}
