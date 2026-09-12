package com.edu.kh.school.features.student;

import com.edu.kh.school.exception.CapacityExceededException;
import com.edu.kh.school.exception.DuplicateResourceException;
import com.edu.kh.school.exception.InvalidStatusException;
import com.edu.kh.school.exception.ResourceNotFoundException;
import com.edu.kh.school.features.auth.User;
import com.edu.kh.school.features.auth.UserRepository;
import com.edu.kh.school.features.schoolclass.ClassStatus;
import com.edu.kh.school.features.schoolclass.SchoolClass;
import com.edu.kh.school.features.schoolclass.SchoolClassRepository;
import com.edu.kh.school.features.student.dto.CreateStudentRequest;
import com.edu.kh.school.features.student.dto.StudentResponse;
import com.edu.kh.school.features.student.dto.UpdateStudentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final SchoolClassRepository classRepository;
    private final UserRepository userRepository;
    private final StudentMapper mapper;

    @Override
    @Transactional
    public StudentResponse createStudent(CreateStudentRequest request) {
        String studentCode = request.studentCode().trim().toUpperCase();
        String email = request.email().trim().toLowerCase();

        if (studentRepository.existsByStudentCode(studentCode)) {
            throw new DuplicateResourceException("Student code already exists: " + studentCode);
        }

        if (studentRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Student email already exists: " + email);
        }

        SchoolClass schoolClass = null;
        if (request.classId() != null) {
            schoolClass = classRepository.findById(request.classId())
                    .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + request.classId()));

            if (schoolClass.getStatus() != ClassStatus.ACTIVE) {
                throw new InvalidStatusException("Cannot enroll student in inactive class: " + schoolClass.getName());
            }

            long currentCount = studentRepository.countByCurrentClassIdAndStatus(request.classId(), StudentStatus.ACTIVE);
            if (currentCount >= schoolClass.getCapacity()) {
                throw new CapacityExceededException("Class " + schoolClass.getName() + " has reached maximum capacity of " + schoolClass.getCapacity());
            }
        }

        Student student = mapper.toEntity(request, schoolClass);

        if (request.userId() != null) {
            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.userId()));
            student.setUser(user);
        }

        Student saved = studentRepository.save(student);
        log.info("Created student with id: {} and code: {}", saved.getId(), saved.getStudentCode());

        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse getStudentById(UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return mapper.toDto(student);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse getStudentByCode(String code) {
        Student student = studentRepository.findByStudentCode(code.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with code: " + code));
        return mapper.toDto(student);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentResponse> getAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable)
                .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentResponse> searchStudents(String keyword, UUID classId, StudentStatus status, Pageable pageable) {
        return studentRepository.searchStudents(
                keyword != null && !keyword.isBlank() ? keyword.trim() : null,
                classId,
                status,
                pageable
        ).map(mapper::toDto);
    }

    @Override
    @Transactional
    public StudentResponse updateStudent(UUID id, UpdateStudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        String email = request.email().trim().toLowerCase();
        if (!student.getEmail().equalsIgnoreCase(email) && studentRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Student email already exists: " + email);
        }

        SchoolClass schoolClass = null;
        if (request.classId() != null) {
            schoolClass = classRepository.findById(request.classId())
                    .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + request.classId()));

            if (schoolClass.getStatus() != ClassStatus.ACTIVE) {
                throw new InvalidStatusException("Cannot enroll student in inactive class");
            }

            // Check capacity if moving to a different class
            if (student.getCurrentClass() == null || !student.getCurrentClass().getId().equals(request.classId())) {
                long currentCount = studentRepository.countByCurrentClassIdAndStatus(request.classId(), StudentStatus.ACTIVE);
                if (currentCount >= schoolClass.getCapacity()) {
                    throw new CapacityExceededException("Class " + schoolClass.getName() + " has reached maximum capacity");
                }
            }
        }

        student.setFullName(request.fullName().trim());
        student.setEmail(email);
        student.setPhone(request.phone() != null ? request.phone().trim() : null);
        student.setDateOfBirth(request.dateOfBirth());
        student.setSex(request.sex());
        student.setAddress(request.address() != null ? request.address().trim() : null);
        student.setAdmissionDate(request.admissionDate());
        student.setStatus(request.status());
        student.setCurrentClass(schoolClass);

        if (request.userId() != null) {
            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.userId()));
            student.setUser(user);
        }

        Student saved = studentRepository.save(student);
        log.info("Updated student with id: {}", saved.getId());

        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public StudentResponse changeStudentStatus(UUID id, StudentStatus status) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        student.setStatus(status);
        Student saved = studentRepository.save(student);
        log.info("Changed student {} status to: {}", id, status);

        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteStudent(UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        student.setStatus(StudentStatus.INACTIVE);
        studentRepository.save(student);
        log.info("Soft-deleted student (marked INACTIVE) with id: {}", id);
    }
}
