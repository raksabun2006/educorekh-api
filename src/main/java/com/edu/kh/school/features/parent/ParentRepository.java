package com.edu.kh.school.features.parent;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ParentRepository extends JpaRepository<Parent, UUID> {

    boolean existsByEmail(String email);

    Optional<Parent> findByEmail(String email);

    @Query("SELECT p FROM Parent p WHERE " +
            "(:keyword IS NULL OR LOWER(p.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:status IS NULL OR p.status = :status)")
    Page<Parent> searchParents(
            @Param("keyword") String keyword,
            @Param("status") ParentStatus status,
            Pageable pageable
    );
}
