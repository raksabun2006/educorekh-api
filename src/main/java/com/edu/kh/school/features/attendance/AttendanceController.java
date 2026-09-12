package com.edu.kh.school.features.attendance;

import com.edu.kh.school.features.attendance.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return attendanceService.getClassAttendance(classId, date);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<AttendanceResponse> getAttendanceByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Pageable pageable
    ) {
        return attendanceService.getAttendanceByDate(date, pageable);
    }

    @GetMapping("/summary/class/{classId}")
    @ResponseStatus(HttpStatus.OK)
    public AttendanceSummaryResponse getAttendanceSummary(
            @PathVariable UUID classId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return attendanceService.getAttendanceSummary(classId, startDate, endDate);
    }
}
