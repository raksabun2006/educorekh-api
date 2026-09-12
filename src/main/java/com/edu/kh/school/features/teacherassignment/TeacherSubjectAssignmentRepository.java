package com.edu.kh.school.features.teacherassignment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeacherSubjectAssignmentRepository extends JpaRepository<TeacherSubjectAssignment, UUID> {

    boolean existsByTeacherIdAndSubjectIdAndSchoolClassIdAndAcademicYearId(
            UUID teacherId, UUID subjectId, UUID classId, UUID academicYearId
    );

    boolean existsBySubjectIdAndSchoolClassIdAndAcademicYearId(
            UUID subjectId, UUID classId, UUID academicYearId
    );

    List<TeacherSubjectAssignment> findAllByTeacherIdAndAcademicYearId(UUID teacherId, UUID academicYearId);

    List<TeacherSubjectAssignment> findAllByTeacherId(UUID teacherId);

    List<TeacherSubjectAssignment> findAllBySchoolClassIdAndAcademicYearId(UUID classId, UUID academicYearId);

    List<TeacherSubjectAssignment> findAllBySubjectIdAndAcademicYearId(UUID subjectId, UUID academicYearId);
}
