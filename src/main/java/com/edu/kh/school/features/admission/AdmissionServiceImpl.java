package com.edu.kh.school.features.admission;

import com.edu.kh.school.exception.BusinessException;
import com.edu.kh.school.exception.InvalidStatusException;
import com.edu.kh.school.exception.ResourceNotFoundException;
import com.edu.kh.school.features.academic.AcademicYear;
import com.edu.kh.school.features.academic.AcademicYearRepository;
import com.edu.kh.school.features.admission.dto.AdmissionApplicationResponse;
import com.edu.kh.school.features.admission.dto.ApproveAdmissionRequest;
import com.edu.kh.school.features.admission.dto.ReviewAdmissionRequest;
import com.edu.kh.school.features.admission.dto.SubmitAdmissionRequest;
import com.edu.kh.school.features.enrollment.Enrollment;
import com.edu.kh.school.features.enrollment.EnrollmentRepository;
import com.edu.kh.school.features.enrollment.EnrollmentStatus;
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
import java.time.Year;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdmissionServiceImpl implements AdmissionService {

    private final AdmissionApplicationRepository admissionRepository;
    private final AcademicYearRepository academicYearRepository;
    private final StudentRepository studentRepository;
    private final SchoolClassRepository classRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AdmissionMapper mapper;

    @Override
    @Transactional
    public AdmissionApplicationResponse submitApplication(SubmitAdmissionRequest request) {
        AcademicYear academicYear = academicYearRepository.findById(request.academicYearId())
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found with id: " + request.academicYearId()));

        long appCount = admissionRepository.countByAcademicYearId(academicYear.getId()) + 1;
        String applicationNumber = String.format("ADM-%d-%04d", Year.now().getValue(), appCount);

        // In case of collision, append random digits
        while (admissionRepository.existsByApplicationNumber(applicationNumber)) {
            applicationNumber = String.format("ADM-%d-%04d-%d", Year.now().getValue(), appCount, (int) (Math.random() * 1000));
        }

        AdmissionApplication application = mapper.toEntity(request, academicYear, applicationNumber);
        AdmissionApplication saved = admissionRepository.save(application);
        log.info("Submitted admission application {} for applicant {}", saved.getApplicationNumber(), saved.getApplicantFullName());

        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AdmissionApplicationResponse getApplicationById(UUID id) {
        AdmissionApplication application = admissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admission application not found with id: " + id));
        return mapper.toDto(application);
    }

    @Override
    @Transactional(readOnly = true)
    public AdmissionApplicationResponse getApplicationByNumber(String applicationNumber) {
        AdmissionApplication application = admissionRepository.findByApplicationNumber(applicationNumber.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Admission application not found with number: " + applicationNumber));
        return mapper.toDto(application);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdmissionApplicationResponse> searchApplications(
            String keyword, UUID academicYearId, AdmissionStatus status, Pageable pageable
    ) {
        return admissionRepository.searchApplications(
                keyword != null && !keyword.isBlank() ? keyword.trim() : null,
                academicYearId,
                status,
                pageable
        ).map(mapper::toDto);
    }

    @Override
    @Transactional
    public AdmissionApplicationResponse reviewApplication(UUID id, ReviewAdmissionRequest request, String reviewedBy) {
        AdmissionApplication application = admissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admission application not found with id: " + id));

        if (application.getStatus() == AdmissionStatus.APPROVED) {
            throw new InvalidStatusException("Cannot modify already APPROVED application");
        }

        application.setStatus(request.status());
        if (request.reviewNotes() != null) {
            application.setReviewNotes(request.reviewNotes());
        }
        application.setReviewedBy(reviewedBy);

        AdmissionApplication saved = admissionRepository.save(application);
        log.info("Reviewed application {} to status {}", application.getApplicationNumber(), request.status());

        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public AdmissionApplicationResponse approveApplication(UUID id, ApproveAdmissionRequest request, String reviewedBy) {
        AdmissionApplication application = admissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admission application not found with id: " + id));

        if (application.getStatus() == AdmissionStatus.APPROVED) {
            throw new InvalidStatusException("Application is already approved");
        }

        // Generate student code if not provided
        String studentCode = request != null && request.studentCode() != null && !request.studentCode().isBlank()
                ? request.studentCode().trim().toUpperCase()
                : "STU-" + Year.now().getValue() + "-" + String.format("%04d", (int) (Math.random() * 9000 + 1000));

        while (studentRepository.existsByStudentCode(studentCode)) {
            studentCode = "STU-" + Year.now().getValue() + "-" + String.format("%04d", (int) (Math.random() * 9000 + 1000));
        }

        SchoolClass targetClass = null;
        if (request != null && request.classId() != null) {
            targetClass = classRepository.findById(request.classId())
                    .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + request.classId()));

            if (targetClass.getStatus() != ClassStatus.ACTIVE) {
                throw new InvalidStatusException("Cannot enroll student in inactive class");
            }
        }

        LocalDate admissionDate = request != null && request.admissionDate() != null
                ? request.admissionDate()
                : LocalDate.now();

        // Create student
        Student student = Student.builder()
                .studentCode(studentCode)
                .fullName(application.getApplicantFullName())
                .email(application.getApplicantEmail())
                .phone(application.getApplicantPhone())
                .dateOfBirth(application.getDateOfBirth())
                .sex(application.getSex())
                .address(application.getAddress())
                .admissionDate(admissionDate)
                .currentClass(targetClass)
                .status(StudentStatus.ACTIVE)
                .build();

        Student savedStudent = studentRepository.save(student);

        // Create enrollment if class is assigned
        if (targetClass != null) {
            Enrollment enrollment = Enrollment.builder()
                    .student(savedStudent)
                    .schoolClass(targetClass)
                    .academicYear(application.getAcademicYear())
                    .enrollmentDate(admissionDate)
                    .status(EnrollmentStatus.ACTIVE)
                    .remarks("Enrolled upon admission approval")
                    .build();

            enrollmentRepository.save(enrollment);
        }

        // Update application
        application.setStatus(AdmissionStatus.APPROVED);
        application.setCreatedStudent(savedStudent);
        application.setReviewedBy(reviewedBy);
        if (request != null && request.reviewNotes() != null) {
            application.setReviewNotes(request.reviewNotes());
        }

        AdmissionApplication savedApp = admissionRepository.save(application);
        log.info("Approved admission application {} and created student {}", savedApp.getApplicationNumber(), savedStudent.getStudentCode());

        return mapper.toDto(savedApp);
    }
}
