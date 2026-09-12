package com.edu.kh.school.features.enrollment;

import com.edu.kh.school.exception.*;
import com.edu.kh.school.features.academic.AcademicYear;
import com.edu.kh.school.features.academic.AcademicYearRepository;
import com.edu.kh.school.features.enrollment.dto.EnrollStudentRequest;
import com.edu.kh.school.features.enrollment.dto.EnrollmentResponse;
import com.edu.kh.school.features.enrollment.dto.TransferStudentRequest;
import com.edu.kh.school.features.schoolclass.ClassStatus;
import com.edu.kh.school.features.schoolclass.SchoolClass;
import com.edu.kh.school.features.schoolclass.SchoolClassRepository;
import com.edu.kh.school.features.student.Student;
import com.edu.kh.school.features.student.StudentRepository;
import com.edu.kh.school.features.student.StudentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final SchoolClassRepository classRepository;
    private final AcademicYearRepository academicYearRepository;
    private final EnrollmentMapper mapper;

    @Override
    @Transactional
    public EnrollmentResponse enrollStudent(EnrollStudentRequest request) {
        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + request.studentId()));

        if (student.getStatus() != StudentStatus.ACTIVE) {
            throw new InvalidStatusException("Cannot enroll student with status: " + student.getStatus());
        }

        SchoolClass schoolClass = classRepository.findById(request.classId())
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + request.classId()));

        if (schoolClass.getStatus() != ClassStatus.ACTIVE) {
            throw new InvalidStatusException("Cannot enroll student in inactive class: " + schoolClass.getName());
        }

        AcademicYear academicYear = academicYearRepository.findById(request.academicYearId())
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found with id: " + request.academicYearId()));

        if (!schoolClass.getAcademicYear().getId().equals(academicYear.getId())) {
            throw new BusinessException("Class does not belong to the specified academic year");
        }

        // Check if student already has active enrollment in this academic year
        if (enrollmentRepository.existsByStudentIdAndAcademicYearIdAndStatus(student.getId(), academicYear.getId(), EnrollmentStatus.ACTIVE)) {
            throw new DuplicateResourceException("Student already has an active enrollment in academic year: " + academicYear.getName());
        }

        // Check class capacity
        long currentEnrolled = enrollmentRepository.countBySchoolClassIdAndStatus(schoolClass.getId(), EnrollmentStatus.ACTIVE);
        if (currentEnrolled >= schoolClass.getCapacity()) {
            throw new CapacityExceededException("Class " + schoolClass.getName() + " has reached its capacity of " + schoolClass.getCapacity());
        }

        Enrollment enrollment = mapper.toEntity(request, student, schoolClass, academicYear);
        Enrollment saved = enrollmentRepository.save(enrollment);

        // Update student's current class
        student.setCurrentClass(schoolClass);
        studentRepository.save(student);

        log.info("Enrolled student {} into class {} for academic year {}", student.getStudentCode(), schoolClass.getName(), academicYear.getName());

        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentResponse getEnrollmentById(UUID id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));
        return mapper.toDto(enrollment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getStudentEnrollments(UUID studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }

        return enrollmentRepository.findAllByStudentId(studentId).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EnrollmentResponse> getClassEnrollments(UUID classId, EnrollmentStatus status, Pageable pageable) {
        if (!classRepository.existsById(classId)) {
            throw new ResourceNotFoundException("Class not found with id: " + classId);
        }

        if (status != null) {
            return enrollmentRepository.findAllBySchoolClassIdAndStatus(classId, status, pageable)
                    .map(mapper::toDto);
        }
        return enrollmentRepository.findAllBySchoolClassId(classId, pageable)
                .map(mapper::toDto);
    }

    @Override
    @Transactional
    public EnrollmentResponse transferStudent(UUID enrollmentId, TransferStudentRequest request) {
        Enrollment currentEnrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));

        if (currentEnrollment.getStatus() != EnrollmentStatus.ACTIVE) {
            throw new InvalidStatusException("Only ACTIVE enrollments can be transferred");
        }

        SchoolClass targetClass = classRepository.findById(request.targetClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Target class not found with id: " + request.targetClassId()));

        if (targetClass.getStatus() != ClassStatus.ACTIVE) {
            throw new InvalidStatusException("Target class is not active");
        }

        if (currentEnrollment.getSchoolClass().getId().equals(targetClass.getId())) {
            throw new BusinessException("Student is already enrolled in class: " + targetClass.getName());
        }

        if (!targetClass.getAcademicYear().getId().equals(currentEnrollment.getAcademicYear().getId())) {
            throw new BusinessException("Target class must belong to the same academic year: " + currentEnrollment.getAcademicYear().getName());
        }

        // Check target class capacity
        long targetEnrolled = enrollmentRepository.countBySchoolClassIdAndStatus(targetClass.getId(), EnrollmentStatus.ACTIVE);
        if (targetEnrolled >= targetClass.getCapacity()) {
            throw new CapacityExceededException("Target class " + targetClass.getName() + " has reached capacity");
        }

        // Mark current enrollment as TRANSFERRED
        currentEnrollment.setStatus(EnrollmentStatus.TRANSFERRED);
        currentEnrollment.setRemarks(request.remarks() != null ? request.remarks() : "Transferred to " + targetClass.getName());
        enrollmentRepository.save(currentEnrollment);

        // Create new enrollment
        Enrollment newEnrollment = Enrollment.builder()
                .student(currentEnrollment.getStudent())
                .schoolClass(targetClass)
                .academicYear(currentEnrollment.getAcademicYear())
                .enrollmentDate(LocalDate.now())
                .status(EnrollmentStatus.ACTIVE)
                .remarks("Transferred from " + currentEnrollment.getSchoolClass().getName())
                .build();

        Enrollment savedNew = enrollmentRepository.save(newEnrollment);

        // Update student's current class
        Student student = currentEnrollment.getStudent();
        student.setCurrentClass(targetClass);
        studentRepository.save(student);

        log.info("Transferred student {} to class {}", student.getStudentCode(), targetClass.getName());

        return mapper.toDto(savedNew);
    }

    @Override
    @Transactional
    public EnrollmentResponse cancelEnrollment(UUID enrollmentId, String remarks) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));

        if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {
            throw new InvalidStatusException("Only ACTIVE enrollments can be cancelled");
        }

        enrollment.setStatus(EnrollmentStatus.CANCELLED);
        if (remarks != null && !remarks.isBlank()) {
            enrollment.setRemarks(remarks);
        }
        Enrollment saved = enrollmentRepository.save(enrollment);

        Student student = enrollment.getStudent();
        if (student.getCurrentClass() != null && student.getCurrentClass().getId().equals(enrollment.getSchoolClass().getId())) {
            student.setCurrentClass(null);
            studentRepository.save(student);
        }

        log.info("Cancelled enrollment {}", enrollmentId);

        return mapper.toDto(saved);
    }
}
