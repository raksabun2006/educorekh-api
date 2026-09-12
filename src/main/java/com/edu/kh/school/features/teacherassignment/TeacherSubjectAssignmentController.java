package com.edu.kh.school.features.teacherassignment;

import com.edu.kh.school.features.teacherassignment.dto.AssignTeacherSubjectRequest;
import com.edu.kh.school.features.teacherassignment.dto.TeacherSubjectAssignmentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/teacher-assignments")
@RequiredArgsConstructor
public class TeacherSubjectAssignmentController {

    private final TeacherSubjectAssignmentService assignmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public TeacherSubjectAssignmentResponse assignTeacher(@Valid @RequestBody AssignTeacherSubjectRequest request) {
        return assignmentService.assignTeacherToSubject(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public void removeAssignment(@PathVariable UUID id) {
        assignmentService.removeTeacherFromSubject(id);
    }

    @GetMapping("/teacher/{teacherId}")
    @ResponseStatus(HttpStatus.OK)
    public List<TeacherSubjectAssignmentResponse> getTeacherAssignments(
            @PathVariable UUID teacherId,
            @RequestParam(required = false) UUID academicYearId
    ) {
        return assignmentService.getTeacherSubjects(teacherId, academicYearId);
    }

    @GetMapping("/class/{classId}")
    @ResponseStatus(HttpStatus.OK)
    public List<TeacherSubjectAssignmentResponse> getClassAssignments(
            @PathVariable UUID classId,
            @RequestParam UUID academicYearId
    ) {
        return assignmentService.getClassSubjects(classId, academicYearId);
    }

    @GetMapping("/subject/{subjectId}")
    @ResponseStatus(HttpStatus.OK)
    public List<TeacherSubjectAssignmentResponse> getSubjectAssignments(
            @PathVariable UUID subjectId,
            @RequestParam UUID academicYearId
    ) {
        return assignmentService.getSubjectTeachers(subjectId, academicYearId);
    }
}
