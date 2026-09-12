package com.edu.kh.school.features.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, UUID> {

    List<ClassSchedule> findAllBySchoolClassIdAndAcademicYearId(UUID classId, UUID academicYearId);

    List<ClassSchedule> findAllByTeacherIdAndAcademicYearId(UUID teacherId, UUID academicYearId);

    @Query("SELECT COUNT(s) > 0 FROM ClassSchedule s WHERE " +
            "s.teacher.id = :teacherId " +
            "AND s.dayOfWeek = :dayOfWeek " +
            "AND s.academicYear.id = :academicYearId " +
            "AND (:excludeId IS NULL OR s.id != :excludeId) " +
            "AND (s.startTime < :endTime AND s.endTime > :startTime)")
    boolean existsTeacherScheduleConflict(
            @Param("teacherId") UUID teacherId,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("academicYearId") UUID academicYearId,
            @Param("excludeId") UUID excludeId
    );

    @Query("SELECT COUNT(s) > 0 FROM ClassSchedule s WHERE " +
            "s.schoolClass.id = :classId " +
            "AND s.dayOfWeek = :dayOfWeek " +
            "AND s.academicYear.id = :academicYearId " +
            "AND (:excludeId IS NULL OR s.id != :excludeId) " +
            "AND (s.startTime < :endTime AND s.endTime > :startTime)")
    boolean existsClassScheduleConflict(
            @Param("classId") UUID classId,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("academicYearId") UUID academicYearId,
            @Param("excludeId") UUID excludeId
    );

    @Query("SELECT COUNT(s) > 0 FROM ClassSchedule s WHERE " +
            "LOWER(s.room) = LOWER(:room) " +
            "AND s.dayOfWeek = :dayOfWeek " +
            "AND s.academicYear.id = :academicYearId " +
            "AND (:excludeId IS NULL OR s.id != :excludeId) " +
            "AND (s.startTime < :endTime AND s.endTime > :startTime)")
    boolean existsRoomScheduleConflict(
            @Param("room") String room,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("academicYearId") UUID academicYearId,
            @Param("excludeId") UUID excludeId
    );
}
