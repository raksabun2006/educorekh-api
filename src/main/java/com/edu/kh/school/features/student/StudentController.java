package com.edu.kh.school.features.student;

import com.edu.kh.school.features.student.dto.CreateStudentRequest;
import com.edu.kh.school.features.student.dto.StudentResponse;
import com.edu.kh.school.features.student.dto.UpdateStudentRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'STAFF')")
    public StudentResponse createStudent(@Valid @RequestBody CreateStudentRequest request) {
        return studentService.createStudent(request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public StudentResponse getStudentById(@PathVariable UUID id) {
        return studentService.getStudentById(id);
    }

    @GetMapping("/code/{code}")
    @ResponseStatus(HttpStatus.OK)
    public StudentResponse getStudentByCode(@PathVariable String code) {
        return studentService.getStudentByCode(code);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<StudentResponse> getAllStudents(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UUID classId,
            @RequestParam(required = false) StudentStatus status,
            Pageable pageable
    ) {
        if (keyword != null || classId != null || status != null) {
            return studentService.searchStudents(keyword, classId, status, pageable);
        }
        return studentService.getAllStudents(pageable);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'STAFF')")
    public StudentResponse updateStudent(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStudentRequest request
    ) {
        return studentService.updateStudent(id, request);
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public StudentResponse changeStudentStatus(
            @PathVariable UUID id,
            @RequestParam StudentStatus status
    ) {
        return studentService.changeStudentStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteStudent(@PathVariable UUID id) {
        studentService.deleteStudent(id);
    }
}
