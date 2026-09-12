package com.edu.kh.school.features.student;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, UUID> {

    boolean existsByStudentCode(String studentCode);

    boolean existsByEmail(String email);

    Optional<Student> findByStudentCode(String studentCode);

    Optional<Student> findByEmail(String email);

    long countByCurrentClassIdAndStatus(UUID classId, StudentStatus status);

    @Query("SELECT s FROM Student s WHERE " +
            "(:keyword IS NULL OR LOWER(s.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.studentCode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:classId IS NULL OR s.currentClass.id = :classId) " +
            "AND (:status IS NULL OR s.status = :status)")
    Page<Student> searchStudents(
            @Param("keyword") String keyword,
            @Param("classId") UUID classId,
            @Param("status") StudentStatus status,
            Pageable pageable
    );
}
