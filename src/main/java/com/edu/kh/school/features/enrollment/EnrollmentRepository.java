package com.edu.kh.school.features.enrollment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {

    boolean existsByStudentIdAndAcademicYearIdAndStatus(UUID studentId, UUID academicYearId, EnrollmentStatus status);

    Optional<Enrollment> findByStudentIdAndAcademicYearIdAndStatus(UUID studentId, UUID academicYearId, EnrollmentStatus status);

    List<Enrollment> findAllByStudentId(UUID studentId);

    Page<Enrollment> findAllBySchoolClassIdAndStatus(UUID classId, EnrollmentStatus status, Pageable pageable);

    Page<Enrollment> findAllBySchoolClassId(UUID classId, Pageable pageable);

    long countBySchoolClassIdAndStatus(UUID classId, EnrollmentStatus status);
}
