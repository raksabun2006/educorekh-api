package com.edu.kh.school.features.student;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, UUID>, JpaSpecificationExecutor<Student> {

    boolean existsByStudentCode(String studentCode);

    boolean existsByEmail(String email);

    Optional<Student> findByStudentCode(String studentCode);

    Optional<Student> findByEmail(String email);

    long countByCurrentClassIdAndStatus(UUID classId, StudentStatus status);
}
