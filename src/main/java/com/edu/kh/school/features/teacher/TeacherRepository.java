package com.edu.kh.school.features.teacher;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface TeacherRepository extends JpaRepository<Teacher, UUID>, JpaSpecificationExecutor<Teacher> {

    boolean existsByTeacherCode(String teacherCode);

    boolean existsByEmail(String email);

    Optional<Teacher> findByTeacherCode(String teacherCode);

    Optional<Teacher> findByEmail(String email);
}
