package com.edu.kh.school.features.academic;

import com.edu.kh.school.features.academic.dto.AcademicYearResponse;
import com.edu.kh.school.features.academic.dto.CreateAcademicYearRequest;
import com.edu.kh.school.features.academic.dto.UpdateAcademicYearRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/academic-years")
@RequiredArgsConstructor
public class AcademicYearController {

    private final AcademicYearService academicYearService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public AcademicYearResponse createAcademicYear(@Valid @RequestBody CreateAcademicYearRequest request) {
        return academicYearService.createAcademicYear(request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AcademicYearResponse getAcademicYearById(@PathVariable UUID id) {
        return academicYearService.getAcademicYearById(id);
    }

    @GetMapping("/current")
    @ResponseStatus(HttpStatus.OK)
    public AcademicYearResponse getCurrentAcademicYear() {
        return academicYearService.getCurrentAcademicYear();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<AcademicYearResponse> getAllAcademicYears(Pageable pageable) {
        return academicYearService.getAllAcademicYears(pageable);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public AcademicYearResponse updateAcademicYear(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAcademicYearRequest request
    ) {
        return academicYearService.updateAcademicYear(id, request);
    }

    @PatchMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public AcademicYearResponse activateAcademicYear(@PathVariable UUID id) {
        return academicYearService.activateAcademicYear(id);
    }

    @PatchMapping("/{id}/complete")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public AcademicYearResponse completeAcademicYear(@PathVariable UUID id) {
        return academicYearService.completeAcademicYear(id);
    }
}
