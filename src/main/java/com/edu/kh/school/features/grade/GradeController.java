package com.edu.kh.school.features.grade;

import com.edu.kh.school.features.grade.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'TEACHER')")
    public GradeResponse recordGrade(
            @Valid @RequestBody RecordGradeRequest request,
            Authentication authentication
    ) {
        String recordedBy = authentication != null ? authentication.getName() : "SYSTEM";
        return gradeService.recordGrade(request, recordedBy);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'TEACHER')")
    public GradeResponse updateGrade(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateGradeRequest request
    ) {
        return gradeService.updateGrade(id, request);
    }

    @GetMapping("/student/{studentId}")
    @ResponseStatus(HttpStatus.OK)
    public List<GradeResponse> getStudentGrades(
            @PathVariable UUID studentId,
            @RequestParam(required = false) UUID academicYearId,
            @RequestParam(required = false) Semester semester
    ) {
        return gradeService.getStudentGrades(studentId, academicYearId, semester);
    }

    @GetMapping("/student/{studentId}/results")
    @ResponseStatus(HttpStatus.OK)
    public StudentSemesterResultResponse getStudentSemesterResult(
            @PathVariable UUID studentId,
            @RequestParam UUID academicYearId,
            @RequestParam Semester semester
    ) {
        return gradeService.getStudentSemesterResult(studentId, academicYearId, semester);
    }

    @GetMapping("/class/{classId}/results")
    @ResponseStatus(HttpStatus.OK)
    public ClassResultResponse getClassResults(
            @PathVariable UUID classId,
            @RequestParam UUID academicYearId,
            @RequestParam Semester semester
    ) {
        return gradeService.getClassResults(classId, academicYearId, semester);
    }
}
