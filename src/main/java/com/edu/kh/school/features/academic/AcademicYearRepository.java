package com.edu.kh.school.features.academic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AcademicYearRepository extends JpaRepository<AcademicYear, UUID> {
    boolean existsByName(String name);

    Optional<AcademicYear> findByName(String name);

    Optional<AcademicYear> findByStatus(AcademicYearStatus status);

    List<AcademicYear> findAllByStatus(AcademicYearStatus status);

    Page<AcademicYear> findAll(Pageable pageable);
}
