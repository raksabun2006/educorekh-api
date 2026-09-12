package com.edu.kh.school.features.teacher;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface TeacherRepository extends JpaRepository<Teacher, UUID> {

    boolean existsByTeacherCode(String teacherCode);

    boolean existsByEmail(String email);

    Optional<Teacher> findByTeacherCode(String teacherCode);

    Optional<Teacher> findByEmail(String email);

    @Query("SELECT t FROM Teacher t WHERE " +
            "(:keyword IS NULL OR LOWER(t.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.teacherCode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.specialization) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:status IS NULL OR t.status = :status)")
    Page<Teacher> searchTeachers(@Param("keyword") String keyword,
                                 @Param("status") TeacherStatus status,
                                 Pageable pageable);
}
