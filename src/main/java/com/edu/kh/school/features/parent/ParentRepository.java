package com.edu.kh.school.features.parent;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ParentRepository extends JpaRepository<Parent, UUID>, JpaSpecificationExecutor<Parent> {

    boolean existsByEmail(String email);

    Optional<Parent> findByEmail(String email);
}
