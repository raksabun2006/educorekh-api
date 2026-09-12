package com.edu.kh.school.features.enrollment;

import com.edu.kh.school.features.enrollment.dto.EnrollStudentRequest;
import com.edu.kh.school.features.enrollment.dto.EnrollmentResponse;
import com.edu.kh.school.features.enrollment.dto.TransferStudentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface EnrollmentService {

    EnrollmentResponse enrollStudent(EnrollStudentRequest request);

    EnrollmentResponse getEnrollmentById(UUID id);

    List<EnrollmentResponse> getStudentEnrollments(UUID studentId);

    Page<EnrollmentResponse> getClassEnrollments(UUID classId, EnrollmentStatus status, Pageable pageable);

    EnrollmentResponse transferStudent(UUID enrollmentId, TransferStudentRequest request);

    EnrollmentResponse cancelEnrollment(UUID enrollmentId, String remarks);
}
