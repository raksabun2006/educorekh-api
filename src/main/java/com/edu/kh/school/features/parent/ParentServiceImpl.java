package com.edu.kh.school.features.parent;

import com.edu.kh.school.exception.DuplicateResourceException;
import com.edu.kh.school.exception.ResourceNotFoundException;
import com.edu.kh.school.features.parent.dto.CreateParentRequest;
import com.edu.kh.school.features.parent.dto.ParentResponse;
import com.edu.kh.school.features.parent.dto.UpdateParentRequest;
import com.edu.kh.school.features.student.Student;
import com.edu.kh.school.features.student.StudentMapper;
import com.edu.kh.school.features.student.StudentRepository;
import com.edu.kh.school.features.student.dto.StudentResponse;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParentServiceImpl implements ParentService {

    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;
    private final ParentMapper mapper;
    private final StudentMapper studentMapper;

    @Override
    @Transactional
    public ParentResponse createParent(CreateParentRequest request) {
        String email = request.email().trim().toLowerCase();

        if (parentRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Parent email already exists: " + email);
        }

        Parent parent = mapper.toEntity(request);

        if (request.studentIds() != null && !request.studentIds().isEmpty()) {
            Set<Student> students = new HashSet<>(studentRepository.findAllById(request.studentIds()));
            parent.setChildren(students);
        }

        Parent saved = parentRepository.save(parent);
        log.info("Created parent with id: {}", saved.getId());

        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ParentResponse getParentById(UUID id) {
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found with id: " + id));
        return mapper.toDto(parent);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ParentResponse> getAllParents(Pageable pageable) {
        return searchParents(null, null, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ParentResponse> searchParents(String keyword, ParentStatus status, Pageable pageable) {
        Specification<Parent> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                Predicate nameMatch = cb.like(cb.lower(root.get("fullName")), pattern);
                Predicate emailMatch = cb.like(cb.lower(root.get("email")), pattern);
                Predicate phoneMatch = cb.like(cb.lower(root.get("phone")), pattern);
                predicates.add(cb.or(nameMatch, emailMatch, phoneMatch));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (query != null && Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("children", JoinType.LEFT);
            }

            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };

        return parentRepository.findAll(spec, pageable).map(mapper::toDto);
    }

    @Override
    @Transactional
    public ParentResponse updateParent(UUID id, UpdateParentRequest request) {
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found with id: " + id));

        String email = request.email().trim().toLowerCase();
        if (!parent.getEmail().equalsIgnoreCase(email) && parentRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Parent email already exists: " + email);
        }

        parent.setFullName(request.fullName().trim());
        parent.setEmail(email);
        parent.setPhone(request.phone() != null ? request.phone().trim() : null);
        parent.setOccupation(request.occupation() != null ? request.occupation().trim() : null);
        parent.setAddress(request.address() != null ? request.address().trim() : null);
        parent.setStatus(request.status());

        if (request.studentIds() != null) {
            Set<Student> students = new HashSet<>(studentRepository.findAllById(request.studentIds()));
            parent.setChildren(students);
        }

        Parent saved = parentRepository.save(parent);
        log.info("Updated parent with id: {}", saved.getId());

        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public ParentResponse linkStudentToParent(UUID parentId, UUID studentId) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found with id: " + parentId));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        parent.getChildren().add(student);
        Parent saved = parentRepository.save(parent);
        log.info("Linked student {} to parent {}", studentId, parentId);

        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public ParentResponse unlinkStudentFromParent(UUID parentId, UUID studentId) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found with id: " + parentId));

        parent.getChildren().removeIf(s -> s.getId().equals(studentId));
        Parent saved = parentRepository.save(parent);
        log.info("Unlinked student {} from parent {}", studentId, parentId);

        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getChildrenByParent(UUID parentId) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found with id: " + parentId));

        return parent.getChildren().stream()
                .map(studentMapper::toDto)
                .toList();
    }
}
