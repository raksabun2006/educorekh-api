package com.edu.kh.school.features.admission;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface AdmissionApplicationRepository extends JpaRepository<AdmissionApplication, UUID>, JpaSpecificationExecutor<AdmissionApplication> {

    boolean existsByApplicationNumber(String applicationNumber);

    Optional<AdmissionApplication> findByApplicationNumber(String applicationNumber);

    long countByAcademicYearId(UUID academicYearId);
}
