package com.edu.kh.school.features.enrollment;

import com.edu.kh.school.features.enrollment.dto.EnrollStudentRequest;
import com.edu.kh.school.features.enrollment.dto.EnrollmentResponse;
import com.edu.kh.school.features.enrollment.dto.TransferStudentRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'STAFF')")
    public EnrollmentResponse enrollStudent(@Valid @RequestBody EnrollStudentRequest request) {
        return enrollmentService.enrollStudent(request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EnrollmentResponse getEnrollmentById(@PathVariable UUID id) {
        return enrollmentService.getEnrollmentById(id);
    }

    @GetMapping("/student/{studentId}")
    @ResponseStatus(HttpStatus.OK)
    public List<EnrollmentResponse> getStudentEnrollments(@PathVariable UUID studentId) {
        return enrollmentService.getStudentEnrollments(studentId);
    }

    @GetMapping("/class/{classId}")
    @ResponseStatus(HttpStatus.OK)
    public Page<EnrollmentResponse> getClassEnrollments(
            @PathVariable UUID classId,
            @RequestParam(required = false) EnrollmentStatus status,
            @ParameterObject Pageable pageable
    ) {
        return enrollmentService.getClassEnrollments(classId, status, pageable);
    }

    @PostMapping("/{id}/transfer")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'STAFF')")
    public EnrollmentResponse transferStudent(
            @PathVariable UUID id,
            @Valid @RequestBody TransferStudentRequest request
    ) {
        return enrollmentService.transferStudent(id, request);
    }

    @PatchMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'STAFF')")
    public EnrollmentResponse cancelEnrollment(
            @PathVariable UUID id,
            @RequestParam(required = false) String remarks
    ) {
        return enrollmentService.cancelEnrollment(id, remarks);
    }
}
