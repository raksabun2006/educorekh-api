package com.edu.kh.school.features.subject;

import com.edu.kh.school.exception.DuplicateResourceException;
import com.edu.kh.school.exception.ResourceNotFoundException;
import com.edu.kh.school.features.subject.dto.CreateSubjectRequest;
import com.edu.kh.school.features.subject.dto.SubjectResponse;
import com.edu.kh.school.features.subject.dto.UpdateSubjectRequest;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final SubjectMapper mapper;

    @Override
    @Transactional
    public SubjectResponse createSubject(CreateSubjectRequest request) {
        String code = request.code().trim().toUpperCase();

        if (subjectRepository.existsByCode(code)) {
            throw new DuplicateResourceException("Subject code already exists: " + code);
        }

        Subject subject = mapper.toEntity(request);
        Subject saved = subjectRepository.save(subject);
        log.info("Created subject with id: {} and code: {}", saved.getId(), saved.getCode());

        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectResponse getSubjectById(UUID id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));
        return mapper.toDto(subject);
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectResponse getSubjectByCode(String code) {
        Subject subject = subjectRepository.findByCode(code.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with code: " + code));
        return mapper.toDto(subject);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubjectResponse> getAllSubjects(Pageable pageable) {
        return searchSubjects(null, null, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubjectResponse> searchSubjects(String keyword, SubjectStatus status, Pageable pageable) {
        Specification<Subject> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                Predicate nameMatch = cb.like(cb.lower(root.get("name")), pattern);
                Predicate codeMatch = cb.like(cb.lower(root.get("code")), pattern);
                predicates.add(cb.or(nameMatch, codeMatch));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };

        return subjectRepository.findAll(spec, pageable).map(mapper::toDto);
    }

    @Override
    @Transactional
    public SubjectResponse updateSubject(UUID id, UpdateSubjectRequest request) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));

        subject.setName(request.name().trim());
        subject.setDescription(request.description() != null ? request.description().trim() : null);
        subject.setCredit(request.credit());
        subject.setStatus(request.status());

        Subject saved = subjectRepository.save(subject);
        log.info("Updated subject with id: {}", saved.getId());

        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public SubjectResponse changeSubjectStatus(UUID id, SubjectStatus status) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));

        subject.setStatus(status);
        Subject saved = subjectRepository.save(subject);
        log.info("Changed subject {} status to: {}", id, status);

        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteSubject(UUID id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));

        subject.setStatus(SubjectStatus.INACTIVE);
        subjectRepository.save(subject);
        log.info("Soft-deleted subject (marked INACTIVE) with id: {}", id);
    }
}
