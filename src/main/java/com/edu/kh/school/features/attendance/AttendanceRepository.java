package com.edu.kh.school.features.attendance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRepository extends JpaRepository<Attendance, UUID>, JpaSpecificationExecutor<Attendance> {

    boolean existsByStudentIdAndDate(UUID studentId, LocalDate date);

    Optional<Attendance> findByStudentIdAndDate(UUID studentId, LocalDate date);

    List<Attendance> findAllByStudentIdAndDateBetweenOrderByDateDesc(UUID studentId, LocalDate startDate, LocalDate endDate);

    List<Attendance> findAllByStudentIdOrderByDateDesc(UUID studentId);

    List<Attendance> findAllBySchoolClassIdAndDate(UUID classId, LocalDate date);

    List<Attendance> findAllBySchoolClassIdAndDateBetween(UUID classId, LocalDate startDate, LocalDate endDate);

    List<Attendance> findAllBySchoolClassIdAndDateBetweenOrderByDateAsc(UUID classId, LocalDate startDate, LocalDate endDate);

    List<Attendance> findAllByDateBetweenOrderByDateAsc(LocalDate startDate, LocalDate endDate);

    Page<Attendance> findAllByDate(LocalDate date, Pageable pageable);

    long countBySchoolClassIdAndDateBetweenAndStatus(UUID classId, LocalDate startDate, LocalDate endDate, AttendanceStatus status);

    long countByDateBetweenAndStatus(LocalDate startDate, LocalDate endDate, AttendanceStatus status);
}
