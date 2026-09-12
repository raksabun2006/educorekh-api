package com.edu.kh.school.features.schoolclass;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SchoolClassRepository extends JpaRepository<SchoolClass, UUID> {

    boolean existsByNameAndAcademicYearId(String name, UUID academicYearId);

    Optional<SchoolClass> findByNameAndAcademicYearId(String name, UUID academicYearId);

    @Query("SELECT c FROM SchoolClass c WHERE " +
            "(:keyword IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.room) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.gradeLevel) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:academicYearId IS NULL OR c.academicYear.id = :academicYearId) " +
            "AND (:status IS NULL OR c.status = :status)")
    Page<SchoolClass> searchClasses(
            @Param("keyword") String keyword,
            @Param("academicYearId") UUID academicYearId,
            @Param("status") ClassStatus status,
            Pageable pageable
    );
}
