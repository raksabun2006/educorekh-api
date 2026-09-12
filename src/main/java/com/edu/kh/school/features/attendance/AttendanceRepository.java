package com.edu.kh.school.features.attendance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {

    boolean existsByStudentIdAndDate(UUID studentId, LocalDate date);

    Optional<Attendance> findByStudentIdAndDate(UUID studentId, LocalDate date);

    List<Attendance> findAllByStudentIdAndDateBetweenOrderByDateDesc(UUID studentId, LocalDate startDate, LocalDate endDate);

    List<Attendance> findAllByStudentIdOrderByDateDesc(UUID studentId);

    List<Attendance> findAllBySchoolClassIdAndDate(UUID classId, LocalDate date);

    List<Attendance> findAllBySchoolClassIdAndDateBetween(UUID classId, LocalDate startDate, LocalDate endDate);

    Page<Attendance> findAllByDate(LocalDate date, Pageable pageable);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.schoolClass.id = :classId AND a.date BETWEEN :startDate AND :endDate AND a.status = :status")
    long countByClassAndDateBetweenAndStatus(
            @Param("classId") UUID classId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") AttendanceStatus status
    );
}
