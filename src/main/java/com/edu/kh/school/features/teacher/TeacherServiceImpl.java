package com.edu.kh.school.features.teacher;

import com.edu.kh.school.exception.DuplicateResourceException;
import com.edu.kh.school.exception.ResourceNotFoundException;
import com.edu.kh.school.features.auth.User;
import com.edu.kh.school.features.auth.UserRepository;
import com.edu.kh.school.features.teacher.dto.CreateTeacherRequest;
import com.edu.kh.school.features.teacher.dto.TeacherResponse;
import com.edu.kh.school.features.teacher.dto.TeacherSummaryResponse;
import com.edu.kh.school.features.teacher.dto.UpdateTeacherRequest;
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
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final TeacherMapper mapper;

    @Override
    @Transactional
    public TeacherResponse createTeacher(CreateTeacherRequest request) {
        String teacherCode = request.teacherCode().trim().toUpperCase();
        String email = request.email().trim().toLowerCase();

        if (teacherRepository.existsByTeacherCode(teacherCode)) {
            throw new DuplicateResourceException("Teacher code already exists: " + teacherCode);
        }

        if (teacherRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Teacher email already exists: " + email);
        }

        Teacher teacher = mapper.toEntity(request);

        if (request.userId() != null) {
            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.userId()));
            teacher.setUser(user);
        }

        Teacher saved = teacherRepository.save(teacher);
        log.info("Created teacher with id: {} and code: {}", saved.getId(), saved.getTeacherCode());

        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherResponse getTeacherById(UUID id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));
        return mapper.toDto(teacher);
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherResponse getTeacherByCode(String code) {
        Teacher teacher = teacherRepository.findByTeacherCode(code.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with code: " + code));
        return mapper.toDto(teacher);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TeacherResponse> getAllTeachers(Pageable pageable) {
        return searchTeachers(null, null, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TeacherResponse> searchTeachers(String keyword, TeacherStatus status, Pageable pageable) {
        Specification<Teacher> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                Predicate nameMatch = cb.like(cb.lower(root.get("fullName")), pattern);
                Predicate codeMatch = cb.like(cb.lower(root.get("teacherCode")), pattern);
                Predicate emailMatch = cb.like(cb.lower(root.get("email")), pattern);
                Predicate specMatch = cb.like(cb.lower(root.get("specialization")), pattern);
                predicates.add(cb.or(nameMatch, codeMatch, emailMatch, specMatch));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };

        return teacherRepository.findAll(spec, pageable).map(mapper::toDto);
    }

    @Override
    @Transactional
    public TeacherResponse updateTeacher(UUID id, UpdateTeacherRequest request) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));

        String email = request.email().trim().toLowerCase();
        if (!teacher.getEmail().equalsIgnoreCase(email) && teacherRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Teacher email already exists: " + email);
        }

        teacher.setFullName(request.fullName().trim());
        teacher.setEmail(email);
        teacher.setPhone(request.phone() != null ? request.phone().trim() : null);
        teacher.setSex(request.sex());
        teacher.setDateOfBirth(request.dateOfBirth());
        teacher.setSpecialization(request.specialization() != null ? request.specialization().trim() : null);
        teacher.setHireDate(request.hireDate());
        teacher.setStatus(request.status());

        if (request.userId() != null) {
            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.userId()));
            teacher.setUser(user);
        }

        Teacher saved = teacherRepository.save(teacher);
        log.info("Updated teacher with id: {}", saved.getId());

        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public TeacherResponse changeTeacherStatus(UUID id, TeacherStatus status) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));

        teacher.setStatus(status);
        Teacher saved = teacherRepository.save(teacher);
        log.info("Changed teacher {} status to: {}", id, status);

        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteTeacher(UUID id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));

        teacher.setStatus(TeacherStatus.INACTIVE);
        teacherRepository.save(teacher);
        log.info("Soft-deleted teacher (marked INACTIVE) with id: {}", id);
    }
}
