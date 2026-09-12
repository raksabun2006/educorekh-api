package com.edu.kh.school.features.subject;

import com.edu.kh.school.features.subject.dto.CreateSubjectRequest;
import com.edu.kh.school.features.subject.dto.SubjectResponse;
import com.edu.kh.school.features.subject.dto.UpdateSubjectRequest;
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
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public SubjectResponse createSubject(@Valid @RequestBody CreateSubjectRequest request) {
        return subjectService.createSubject(request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SubjectResponse getSubjectById(@PathVariable UUID id) {
        return subjectService.getSubjectById(id);
    }

    @GetMapping("/code/{code}")
    @ResponseStatus(HttpStatus.OK)
    public SubjectResponse getSubjectByCode(@PathVariable String code) {
        return subjectService.getSubjectByCode(code);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<SubjectResponse> getAllSubjects(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) SubjectStatus status,
            @ParameterObject Pageable pageable
    ) {
        if (keyword != null || status != null) {
            return subjectService.searchSubjects(keyword, status, pageable);
        }
        return subjectService.getAllSubjects(pageable);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public SubjectResponse updateSubject(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSubjectRequest request
    ) {
        return subjectService.updateSubject(id, request);
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public SubjectResponse changeSubjectStatus(
            @PathVariable UUID id,
            @RequestParam SubjectStatus status
    ) {
        return subjectService.changeSubjectStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteSubject(@PathVariable UUID id) {
        subjectService.deleteSubject(id);
    }
}
