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
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public Page<AttendanceResponse> getAttendances(
            LocalDate date,
            LocalDate startDate,
            LocalDate endDate,
            UUID classId,
            UUID studentId,
            AttendanceStatus status,
            Pageable pageable
    ) {
        Specification<Attendance> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (date != null) {
                predicates.add(cb.equal(root.get("date"), date));
            } else {
                if (startDate != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("date"), startDate));
                }
                if (endDate != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("date"), endDate));
                }
            }

            if (classId != null) {
                predicates.add(cb.equal(root.get("schoolClass").get("id"), classId));
            }

            if (studentId != null) {
                predicates.add(cb.equal(root.get("student").get("id"), studentId));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (query != null && Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("student", JoinType.LEFT);
                root.fetch("schoolClass", JoinType.LEFT);
            }

            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };

        return attendanceRepository.findAll(spec, pageable).map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceSummaryResponse getAttendanceSummary(UUID classId, LocalDate startDate, LocalDate endDate) {
        LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(30);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        String className = null;
        long presentCount;
        long absentCount;
        long lateCount;
        long excusedCount;

        if (classId != null) {
            SchoolClass schoolClass = classRepository.findById(classId)
                    .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + classId));
            className = schoolClass.getName();

            presentCount = attendanceRepository.countBySchoolClassIdAndDateBetweenAndStatus(classId, start, end, AttendanceStatus.PRESENT);
            absentCount = attendanceRepository.countBySchoolClassIdAndDateBetweenAndStatus(classId, start, end, AttendanceStatus.ABSENT);
            lateCount = attendanceRepository.countBySchoolClassIdAndDateBetweenAndStatus(classId, start, end, AttendanceStatus.LATE);
            excusedCount = attendanceRepository.countBySchoolClassIdAndDateBetweenAndStatus(classId, start, end, AttendanceStatus.EXCUSED);
        } else {
            presentCount = attendanceRepository.countByDateBetweenAndStatus(start, end, AttendanceStatus.PRESENT);
            absentCount = attendanceRepository.countByDateBetweenAndStatus(start, end, AttendanceStatus.ABSENT);
            lateCount = attendanceRepository.countByDateBetweenAndStatus(start, end, AttendanceStatus.LATE);
            excusedCount = attendanceRepository.countByDateBetweenAndStatus(start, end, AttendanceStatus.EXCUSED);
        }

        long totalRecords = presentCount + absentCount + lateCount + excusedCount;
        double rate = totalRecords > 0 ? (double) (presentCount + lateCount) / totalRecords * 100.0 : 0.0;

        return AttendanceSummaryResponse.builder()
                .classId(classId)
                .className(className)
                .startDate(start)
                .endDate(end)
                .totalRecords(totalRecords)
                .presentCount(presentCount)
                .absentCount(absentCount)
                .lateCount(lateCount)
                .excusedCount(excusedCount)
                .attendanceRatePercentage(Math.round(rate * 100.0) / 100.0)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceTrendResponse> getAttendanceTrends(UUID classId, LocalDate startDate, LocalDate endDate) {
        LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(7);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        List<Attendance> records = classId != null
                ? attendanceRepository.findAllBySchoolClassIdAndDateBetweenOrderByDateAsc(classId, start, end)
                : attendanceRepository.findAllByDateBetweenOrderByDateAsc(start, end);

        Map<LocalDate, List<Attendance>> byDate = records.stream()
                .collect(Collectors.groupingBy(Attendance::getDate, TreeMap::new, Collectors.toList()));

        List<AttendanceTrendResponse> trends = new ArrayList<>();

        // Populate days between start and end (including dates with zero records)
        LocalDate current = start;
        while (!current.isAfter(end)) {
            List<Attendance> dayRecords = byDate.getOrDefault(current, List.of());
            long present = dayRecords.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
            long absent = dayRecords.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count();
            long late = dayRecords.stream().filter(a -> a.getStatus() == AttendanceStatus.LATE).count();
            long excused = dayRecords.stream().filter(a -> a.getStatus() == AttendanceStatus.EXCUSED).count();
            long total = present + absent + late + excused;
            double rate = total > 0 ? (double) (present + late) / total * 100.0 : 0.0;

            trends.add(AttendanceTrendResponse.builder()
                    .date(current)
                    .presentCount(present)
                    .absentCount(absent)
                    .lateCount(late)
                    .excusedCount(excused)
                    .totalRecords(total)
                    .attendanceRatePercentage(Math.round(rate * 100.0) / 100.0)
                    .build());

            current = current.plusDays(1);
        }

        return trends;
    }
}
