package com.edu.kh.school.features.schoolclass;

import com.edu.kh.school.features.schoolclass.dto.ClassResponse;
import com.edu.kh.school.features.schoolclass.dto.CreateClassRequest;
import com.edu.kh.school.features.schoolclass.dto.UpdateClassRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/classes")
@RequiredArgsConstructor
public class SchoolClassController {

    private final SchoolClassService schoolClassService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ClassResponse createClass(@Valid @RequestBody CreateClassRequest request) {
        return schoolClassService.createClass(request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ClassResponse getClassById(@PathVariable UUID id) {
        return schoolClassService.getClassById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<ClassResponse> getAllClasses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UUID academicYearId,
            @RequestParam(required = false) ClassStatus status,
            @ParameterObject Pageable pageable
    ) {
        if (keyword != null || academicYearId != null || status != null) {
            return schoolClassService.searchClasses(keyword, academicYearId, status, pageable);
        }
        return schoolClassService.getAllClasses(pageable);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ClassResponse updateClass(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateClassRequest request
    ) {
        return schoolClassService.updateClass(id, request);
    }

    @PatchMapping("/{id}/teacher/{teacherId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ClassResponse assignClassTeacher(
            @PathVariable UUID id,
            @PathVariable UUID teacherId
    ) {
        return schoolClassService.assignClassTeacher(id, teacherId);
    }

    @DeleteMapping("/{id}/teacher")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ClassResponse removeClassTeacher(@PathVariable UUID id) {
        return schoolClassService.removeClassTeacher(id);
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ClassResponse changeClassStatus(
            @PathVariable UUID id,
            @RequestParam ClassStatus status
    ) {
        return schoolClassService.changeClassStatus(id, status);
    }
}
