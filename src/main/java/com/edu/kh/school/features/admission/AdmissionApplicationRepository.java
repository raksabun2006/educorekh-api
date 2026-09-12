package com.edu.kh.school.features.admission;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AdmissionApplicationRepository extends JpaRepository<AdmissionApplication, UUID> {

    boolean existsByApplicationNumber(String applicationNumber);

    Optional<AdmissionApplication> findByApplicationNumber(String applicationNumber);

    long countByAcademicYearId(UUID academicYearId);

    @Query("SELECT a FROM AdmissionApplication a WHERE " +
            "(:keyword IS NULL OR LOWER(a.applicantFullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(a.applicationNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(a.applicantEmail) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:academicYearId IS NULL OR a.academicYear.id = :academicYearId) " +
            "AND (:status IS NULL OR a.status = :status)")
    Page<AdmissionApplication> searchApplications(
            @Param("keyword") String keyword,
            @Param("academicYearId") UUID academicYearId,
            @Param("status") AdmissionStatus status,
            Pageable pageable
    );
}
