package com.edu.kh.school.features.schoolclass;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface SchoolClassRepository extends JpaRepository<SchoolClass, UUID>, JpaSpecificationExecutor<SchoolClass> {

    boolean existsByNameAndAcademicYearId(String name, UUID academicYearId);

    Optional<SchoolClass> findByNameAndAcademicYearId(String name, UUID academicYearId);
}
