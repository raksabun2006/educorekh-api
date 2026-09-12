package com.edu.kh.school.features.attendance;

import com.edu.kh.school.exception.DuplicateResourceException;
import com.edu.kh.school.exception.InvalidStatusException;
import com.edu.kh.school.exception.ResourceNotFoundException;
import com.edu.kh.school.features.attendance.dto.*;
import com.edu.kh.school.features.schoolclass.SchoolClass;
import com.edu.kh.school.features.schoolclass.SchoolClassRepository;
import com.edu.kh.school.features.student.Student;
import com.edu.kh.school.features.student.StudentRepository;
import com.edu.kh.school.features.student.StudentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final SchoolClassRepository classRepository;
    private final AttendanceMapper mapper;

    @Override
    @Transactional
    public AttendanceResponse recordAttendance(RecordAttendanceRequest request, String recordedBy) {
        if (attendanceRepository.existsByStudentIdAndDate(request.studentId(), request.date())) {
            throw new DuplicateResourceException("Attendance already recorded for student on date: " + request.date());
        }

        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + request.studentId()));

        if (student.getStatus() != StudentStatus.ACTIVE) {
            throw new InvalidStatusException("Cannot record attendance for inactive student");
        }

        SchoolClass schoolClass = classRepository.findById(request.classId())
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + request.classId()));

        Attendance attendance = mapper.toEntity(request, student, schoolClass, recordedBy);
        Attendance saved = attendanceRepository.save(attendance);
        log.info("Recorded attendance for student {} on date {}", student.getStudentCode(), request.date());

        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public List<AttendanceResponse> recordBatchAttendance(BatchRecordAttendanceRequest request, String recordedBy) {
        SchoolClass schoolClass = classRepository.findById(request.classId())
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + request.classId()));

        List<AttendanceResponse> responses = new ArrayList<>();

        for (StudentAttendanceItem item : request.attendances()) {
            // If already exists, update status, else insert new
            Attendance attendance = attendanceRepository.findByStudentIdAndDate(item.studentId(), request.date())
                    .orElseGet(() -> {
                        Student student = studentRepository.findById(item.studentId())
                                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + item.studentId()));
                        return Attendance.builder()
                                .student(student)
                                .schoolClass(schoolClass)
                                .date(request.date())
                                .build();
                    });

            attendance.setStatus(item.status());
            attendance.setRemarks(item.remarks());
            attendance.setRecordedBy(recordedBy);

            Attendance saved = attendanceRepository.save(attendance);
            responses.add(mapper.toDto(saved));
        }

        log.info("Batch recorded {} attendance entries for class {} on date {}", responses.size(), schoolClass.getName(), request.date());
        return responses;
    }

    @Override
    @Transactional
    public AttendanceResponse updateAttendance(UUID id, UpdateAttendanceRequest request) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with id: " + id));

        attendance.setStatus(request.status());
        if (request.remarks() != null) {
            attendance.setRemarks(request.remarks());
        }

        Attendance saved = attendanceRepository.save(attendance);
        log.info("Updated attendance record {}", id);

        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getStudentAttendance(UUID studentId, LocalDate startDate, LocalDate endDate) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }

        if (startDate != null && endDate != null) {
            return attendanceRepository.findAllByStudentIdAndDateBetweenOrderByDateDesc(studentId, startDate, endDate).stream()
                    .map(mapper::toDto)
                    .toList();
        }

        return attendanceRepository.findAllByStudentIdOrderByDateDesc(studentId).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getClassAttendance(UUID classId, LocalDate date) {
        if (!classRepository.existsById(classId)) {
            throw new ResourceNotFoundException("Class not found with id: " + classId);
        }

        return attendanceRepository.findAllBySchoolClassIdAndDate(classId, date).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceResponse> getAttendanceByDate(LocalDate date, Pageable pageable) {
        return attendanceRepository.findAllByDate(date, pageable)
                .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceSummaryResponse getAttendanceSummary(UUID classId, LocalDate startDate, LocalDate endDate) {
        SchoolClass schoolClass = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + classId));

        long presentCount = attendanceRepository.countByClassAndDateBetweenAndStatus(classId, startDate, endDate, AttendanceStatus.PRESENT);
        long absentCount = attendanceRepository.countByClassAndDateBetweenAndStatus(classId, startDate, endDate, AttendanceStatus.ABSENT);
        long lateCount = attendanceRepository.countByClassAndDateBetweenAndStatus(classId, startDate, endDate, AttendanceStatus.LATE);
        long excusedCount = attendanceRepository.countByClassAndDateBetweenAndStatus(classId, startDate, endDate, AttendanceStatus.EXCUSED);

        long totalRecords = presentCount + absentCount + lateCount + excusedCount;
        double rate = totalRecords > 0 ? (double) (presentCount + lateCount) / totalRecords * 100.0 : 0.0;

        return AttendanceSummaryResponse.builder()
                .classId(schoolClass.getId())
                .className(schoolClass.getName())
                .startDate(startDate)
                .endDate(endDate)
                .totalRecords(totalRecords)
                .presentCount(presentCount)
                .absentCount(absentCount)
                .lateCount(lateCount)
                .excusedCount(excusedCount)
                .attendanceRatePercentage(Math.round(rate * 100.0) / 100.0)
                .build();
    }
}
