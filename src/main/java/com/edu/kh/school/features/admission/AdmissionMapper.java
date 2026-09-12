package com.edu.kh.school.features.admission;

import com.edu.kh.school.features.academic.AcademicYear;
import com.edu.kh.school.features.admission.dto.AdmissionApplicationResponse;
import com.edu.kh.school.features.admission.dto.SubmitAdmissionRequest;
import org.springframework.stereotype.Component;

@Component
public class AdmissionMapper {

    public AdmissionApplication toEntity(SubmitAdmissionRequest request, AcademicYear academicYear, String applicationNumber) {
        return AdmissionApplication.builder()
                .applicationNumber(applicationNumber)
                .applicantFullName(request.applicantFullName().trim())
                .applicantEmail(request.applicantEmail().trim().toLowerCase())
                .applicantPhone(request.applicantPhone() != null ? request.applicantPhone().trim() : null)
                .dateOfBirth(request.dateOfBirth())
                .sex(request.sex())
                .address(request.address() != null ? request.address().trim() : null)
                .previousSchool(request.previousSchool() != null ? request.previousSchool().trim() : null)
                .applyingForGrade(request.applyingForGrade().trim())
                .academicYear(academicYear)
                .status(AdmissionStatus.PENDING)
                .build();
    }

    public AdmissionApplicationResponse toDto(AdmissionApplication app) {
        return AdmissionApplicationResponse.builder()
                .id(app.getId())
                .applicationNumber(app.getApplicationNumber())
                .applicantFullName(app.getApplicantFullName())
                .applicantEmail(app.getApplicantEmail())
                .applicantPhone(app.getApplicantPhone())
                .dateOfBirth(app.getDateOfBirth())
                .sex(app.getSex())
                .address(app.getAddress())
                .previousSchool(app.getPreviousSchool())
                .applyingForGrade(app.getApplyingForGrade())
                .academicYearId(app.getAcademicYear().getId())
                .academicYearName(app.getAcademicYear().getName())
                .status(app.getStatus())
                .reviewNotes(app.getReviewNotes())
                .reviewedBy(app.getReviewedBy())
                .createdStudentId(app.getCreatedStudent() != null ? app.getCreatedStudent().getId() : null)
                .createdStudentCode(app.getCreatedStudent() != null ? app.getCreatedStudent().getStudentCode() : null)
                .createdAt(app.getCreatedAt())
                .updatedAt(app.getUpdatedAt())
                .build();
    }
}
