package com.edu.kh.school.features.attendance;

import com.edu.kh.school.features.attendance.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'TEACHER', 'STAFF')")
    public AttendanceResponse recordAttendance(
            @Valid @RequestBody RecordAttendanceRequest request,
            Authentication authentication
    ) {
        String recordedBy = authentication != null ? authentication.getName() : "SYSTEM";
        return attendanceService.recordAttendance(request, recordedBy);
    }

    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'TEACHER', 'STAFF')")
    public List<AttendanceResponse> recordBatchAttendance(
            @Valid @RequestBody BatchRecordAttendanceRequest request,
            Authentication authentication
    ) {
        String recordedBy = authentication != null ? authentication.getName() : "SYSTEM";
        return attendanceService.recordBatchAttendance(request, recordedBy);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'TEACHER', 'STAFF')")
    public AttendanceResponse updateAttendance(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAttendanceRequest request
    ) {
        return attendanceService.updateAttendance(id, request);
    }

    @GetMapping("/student/{studentId}")
    @ResponseStatus(HttpStatus.OK)
    public List<AttendanceResponse> getStudentAttendance(
            @PathVariable UUID studentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return attendanceService.getStudentAttendance(studentId, startDate, endDate);
    }

    @GetMapping("/class/{classId}")
    @ResponseStatus(HttpStatus.OK)
    public List<AttendanceResponse> getClassAttendance(
            @PathVariable UUID classId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate effectiveDate = date != null ? date : LocalDate.now();
        return attendanceService.getClassAttendance(classId, effectiveDate);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<AttendanceResponse> getAttendances(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) UUID classId,
            @RequestParam(required = false) UUID studentId,
            @RequestParam(required = false) AttendanceStatus status,
            @ParameterObject Pageable pageable
    ) {
        return attendanceService.getAttendances(date, startDate, endDate, classId, studentId, status, pageable);
    }

    @GetMapping("/summary")
    @ResponseStatus(HttpStatus.OK)
    public AttendanceSummaryResponse getOverallSummary(
            @RequestParam(required = false) UUID classId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return attendanceService.getAttendanceSummary(classId, startDate, endDate);
    }

    @GetMapping("/summary/class/{classId}")
    @ResponseStatus(HttpStatus.OK)
    public AttendanceSummaryResponse getAttendanceSummary(
            @PathVariable UUID classId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return attendanceService.getAttendanceSummary(classId, startDate, endDate);
    }

    @GetMapping("/trends")
    @ResponseStatus(HttpStatus.OK)
    public List<AttendanceTrendResponse> getAttendanceTrends(
            @RequestParam(required = false) UUID classId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return attendanceService.getAttendanceTrends(classId, startDate, endDate);
    }
}
