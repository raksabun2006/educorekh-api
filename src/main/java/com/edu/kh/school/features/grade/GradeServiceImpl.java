package com.edu.kh.school.features.grade;

import com.edu.kh.school.exception.DuplicateResourceException;
import com.edu.kh.school.exception.InvalidStatusException;
import com.edu.kh.school.exception.ResourceNotFoundException;
import com.edu.kh.school.features.academic.AcademicYear;
import com.edu.kh.school.features.academic.AcademicYearRepository;
import com.edu.kh.school.features.enrollment.Enrollment;
import com.edu.kh.school.features.enrollment.EnrollmentRepository;
import com.edu.kh.school.features.enrollment.EnrollmentStatus;
import com.edu.kh.school.features.grade.dto.*;
import com.edu.kh.school.features.schoolclass.SchoolClass;
import com.edu.kh.school.features.schoolclass.SchoolClassRepository;
import com.edu.kh.school.features.student.Student;
import com.edu.kh.school.features.student.StudentRepository;
import com.edu.kh.school.features.student.StudentStatus;
import com.edu.kh.school.features.subject.Subject;
import com.edu.kh.school.features.subject.SubjectRepository;
import com.edu.kh.school.features.subject.SubjectStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GradeServiceImpl implements GradeService {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final AcademicYearRepository academicYearRepository;
    private final SchoolClassRepository classRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final GradeMapper mapper;

    @Override
    @Transactional
    public GradeResponse recordGrade(RecordGradeRequest request, String recordedBy) {
        if (gradeRepository.existsByStudentIdAndSubjectIdAndAcademicYearIdAndSemester(
                request.studentId(), request.subjectId(), request.academicYearId(), request.semester())) {
            throw new DuplicateResourceException("Grade already recorded for this student, subject, academic year, and semester");
        }

        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + request.studentId()));

        if (student.getStatus() != StudentStatus.ACTIVE) {
            throw new InvalidStatusException("Cannot record grade for inactive student");
        }

        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + request.subjectId()));

        if (subject.getStatus() != SubjectStatus.ACTIVE) {
            throw new InvalidStatusException("Cannot record grade for inactive subject: " + subject.getName());
        }

        AcademicYear academicYear = academicYearRepository.findById(request.academicYearId())
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found with id: " + request.academicYearId()));

        Grade grade = mapper.toEntity(request, student, subject, academicYear, recordedBy);
        Grade saved = gradeRepository.save(grade);
        log.info("Recorded grade {} ({}) for student {}", saved.getScore(), saved.getGradeLetter(), student.getStudentCode());

        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public GradeResponse updateGrade(UUID id, UpdateGradeRequest request) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade record not found with id: " + id));

        grade.setScore(request.score());
        if (request.remarks() != null) {
            grade.setRemarks(request.remarks());
        }

        Grade saved = gradeRepository.save(grade);
        log.info("Updated grade record with id: {}", id);

        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeResponse> getStudentGrades(UUID studentId, UUID academicYearId, Semester semester) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }

        if (academicYearId != null && semester != null) {
            return gradeRepository.findAllByStudentIdAndAcademicYearIdAndSemester(studentId, academicYearId, semester).stream()
                    .map(mapper::toDto)
                    .toList();
        } else if (academicYearId != null) {
            return gradeRepository.findAllByStudentIdAndAcademicYearId(studentId, academicYearId).stream()
                    .map(mapper::toDto)
                    .toList();
        }

        return gradeRepository.findAllByStudentId(studentId).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StudentSemesterResultResponse getStudentSemesterResult(UUID studentId, UUID academicYearId, Semester semester) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        AcademicYear academicYear = academicYearRepository.findById(academicYearId)
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found with id: " + academicYearId));

        List<Grade> grades = gradeRepository.findAllByStudentIdAndAcademicYearIdAndSemester(studentId, academicYearId, semester);
        List<GradeResponse> gradeResponses = grades.stream().map(mapper::toDto).toList();

        if (grades.isEmpty()) {
            return StudentSemesterResultResponse.builder()
                    .studentId(student.getId())
                    .studentCode(student.getStudentCode())
                    .studentName(student.getFullName())
                    .academicYearId(academicYear.getId())
                    .academicYearName(academicYear.getName())
                    .semester(semester)
                    .grades(gradeResponses)
                    .averageScore(0.0)
                    .gpa(0.0)
                    .overallGrade("N/A")
                    .build();
        }

        double totalScore = 0.0;
        double totalWeightedGpa = 0.0;
        int totalCredits = 0;

        for (Grade grade : grades) {
            int credit = grade.getSubject().getCredit() != null ? grade.getSubject().getCredit() : 1;
            totalScore += grade.getScore();
            totalCredits += credit;

            double point = switch (grade.getGradeLetter()) {
                case "A" -> 4.0;
                case "B" -> 3.0;
                case "C" -> 2.0;
                case "D" -> 1.0;
                case "E" -> 0.5;
                default -> 0.0;
            };
            totalWeightedGpa += (point * credit);
        }

        double averageScore = totalScore / grades.size();
        double gpa = totalCredits > 0 ? totalWeightedGpa / totalCredits : 0.0;

        String overallGrade;
        if (averageScore >= 90.0) overallGrade = "A";
        else if (averageScore >= 80.0) overallGrade = "B";
        else if (averageScore >= 70.0) overallGrade = "C";
        else if (averageScore >= 60.0) overallGrade = "D";
        else if (averageScore >= 50.0) overallGrade = "E";
        else overallGrade = "F";

        return StudentSemesterResultResponse.builder()
                .studentId(student.getId())
                .studentCode(student.getStudentCode())
                .studentName(student.getFullName())
                .academicYearId(academicYear.getId())
                .academicYearName(academicYear.getName())
                .semester(semester)
                .grades(gradeResponses)
                .averageScore(Math.round(averageScore * 100.0) / 100.0)
                .gpa(Math.round(gpa * 100.0) / 100.0)
                .overallGrade(overallGrade)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ClassResultResponse getClassResults(UUID classId, UUID academicYearId, Semester semester) {
        SchoolClass schoolClass = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + classId));

        AcademicYear academicYear = academicYearRepository.findById(academicYearId)
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found with id: " + academicYearId));

        // Find all active enrollments for this class
        List<Enrollment> enrollments = enrollmentRepository.findAllBySchoolClassIdAndStatus(classId, EnrollmentStatus.ACTIVE, Pageable.unpaged()).getContent();

        List<StudentSemesterResultResponse> studentResults = new ArrayList<>();
        double classTotalAverage = 0.0;
        int evaluatedStudentsCount = 0;

        for (Enrollment enrollment : enrollments) {
            StudentSemesterResultResponse studentResult = getStudentSemesterResult(enrollment.getStudent().getId(), academicYearId, semester);
            studentResults.add(studentResult);
            if (!studentResult.grades().isEmpty()) {
                classTotalAverage += studentResult.averageScore();
                evaluatedStudentsCount++;
            }
        }

        double classAverage = evaluatedStudentsCount > 0 ? classTotalAverage / evaluatedStudentsCount : 0.0;

        return ClassResultResponse.builder()
                .classId(schoolClass.getId())
                .className(schoolClass.getName())
                .academicYearId(academicYear.getId())
                .academicYearName(academicYear.getName())
                .semester(semester)
                .studentResults(studentResults)
                .classAverageScore(Math.round(classAverage * 100.0) / 100.0)
                .build();
    }
}
