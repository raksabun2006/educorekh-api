package com.edu.kh.school.features.teacher;

import com.edu.kh.school.features.teacher.dto.CreateTeacherRequest;
import com.edu.kh.school.features.teacher.dto.TeacherResponse;
import com.edu.kh.school.features.teacher.dto.UpdateTeacherRequest;
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
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public TeacherResponse createTeacher(@Valid @RequestBody CreateTeacherRequest request) {
        return teacherService.createTeacher(request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TeacherResponse getTeacherById(@PathVariable UUID id) {
        return teacherService.getTeacherById(id);
    }

    @GetMapping("/code/{code}")
    @ResponseStatus(HttpStatus.OK)
    public TeacherResponse getTeacherByCode(@PathVariable String code) {
        return teacherService.getTeacherByCode(code);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<TeacherResponse> getAllTeachers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) TeacherStatus status,
            @ParameterObject Pageable pageable
    ) {
        if (keyword != null || status != null) {
            return teacherService.searchTeachers(keyword, status, pageable);
        }
        return teacherService.getAllTeachers(pageable);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public TeacherResponse updateTeacher(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTeacherRequest request
    ) {
        return teacherService.updateTeacher(id, request);
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public TeacherResponse changeTeacherStatus(
            @PathVariable UUID id,
            @RequestParam TeacherStatus status
    ) {
        return teacherService.changeTeacherStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTeacher(@PathVariable UUID id) {
        teacherService.deleteTeacher(id);
    }
}
