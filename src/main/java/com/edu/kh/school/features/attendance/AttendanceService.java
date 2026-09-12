package com.edu.kh.school.features.attendance;

import com.edu.kh.school.features.attendance.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AttendanceService {

    AttendanceResponse recordAttendance(RecordAttendanceRequest request, String recordedBy);

    List<AttendanceResponse> recordBatchAttendance(BatchRecordAttendanceRequest request, String recordedBy);

    AttendanceResponse updateAttendance(UUID id, UpdateAttendanceRequest request);

    List<AttendanceResponse> getStudentAttendance(UUID studentId, LocalDate startDate, LocalDate endDate);

    List<AttendanceResponse> getClassAttendance(UUID classId, LocalDate date);

    Page<AttendanceResponse> getAttendanceByDate(LocalDate date, Pageable pageable);

    AttendanceSummaryResponse getAttendanceSummary(UUID classId, LocalDate startDate, LocalDate endDate);
}
