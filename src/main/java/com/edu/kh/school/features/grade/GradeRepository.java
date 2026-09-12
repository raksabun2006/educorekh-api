package com.edu.kh.school.features.grade;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GradeRepository extends JpaRepository<Grade, UUID> {

    boolean existsByStudentIdAndSubjectIdAndAcademicYearIdAndSemester(
            UUID studentId, UUID subjectId, UUID academicYearId, Semester semester
    );

    Optional<Grade> findByStudentIdAndSubjectIdAndAcademicYearIdAndSemester(
            UUID studentId, UUID subjectId, UUID academicYearId, Semester semester
    );

    List<Grade> findAllByStudentIdAndAcademicYearIdAndSemester(UUID studentId, UUID academicYearId, Semester semester);

    List<Grade> findAllByStudentIdAndAcademicYearId(UUID studentId, UUID academicYearId);

    List<Grade> findAllByStudentId(UUID studentId);

    List<Grade> findAllBySubjectIdAndAcademicYearIdAndSemester(UUID subjectId, UUID academicYearId, Semester semester);
}
