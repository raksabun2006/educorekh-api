package com.edu.kh.school.features.teacherassignment;

import com.edu.kh.school.exception.DuplicateResourceException;
import com.edu.kh.school.exception.InvalidStatusException;
import com.edu.kh.school.exception.ResourceNotFoundException;
import com.edu.kh.school.features.academic.AcademicYear;
import com.edu.kh.school.features.academic.AcademicYearRepository;
import com.edu.kh.school.features.schoolclass.ClassStatus;
import com.edu.kh.school.features.schoolclass.SchoolClass;
import com.edu.kh.school.features.schoolclass.SchoolClassRepository;
import com.edu.kh.school.features.subject.Subject;
import com.edu.kh.school.features.subject.SubjectRepository;
import com.edu.kh.school.features.subject.SubjectStatus;
import com.edu.kh.school.features.teacher.Teacher;
import com.edu.kh.school.features.teacher.TeacherRepository;
import com.edu.kh.school.features.teacher.TeacherStatus;
import com.edu.kh.school.features.teacherassignment.dto.AssignTeacherSubjectRequest;
import com.edu.kh.school.features.teacherassignment.dto.TeacherSubjectAssignmentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeacherSubjectAssignmentServiceImpl implements TeacherSubjectAssignmentService {

    private final TeacherSubjectAssignmentRepository assignmentRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final SchoolClassRepository classRepository;
    private final AcademicYearRepository academicYearRepository;
    private final TeacherSubjectAssignmentMapper mapper;

    @Override
    @Transactional
    public TeacherSubjectAssignmentResponse assignTeacherToSubject(AssignTeacherSubjectRequest request) {
        Teacher teacher = teacherRepository.findById(request.teacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + request.teacherId()));

        if (teacher.getStatus() != TeacherStatus.ACTIVE) {
            throw new InvalidStatusException("Cannot assign inactive teacher (Status: " + teacher.getStatus() + ")");
        }

        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + request.subjectId()));

        if (subject.getStatus() != SubjectStatus.ACTIVE) {
            throw new InvalidStatusException("Cannot assign inactive subject: " + subject.getName());
        }

        SchoolClass schoolClass = classRepository.findById(request.classId())
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + request.classId()));

        if (schoolClass.getStatus() != ClassStatus.ACTIVE) {
            throw new InvalidStatusException("Cannot assign to inactive class: " + schoolClass.getName());
        }

        AcademicYear academicYear = academicYearRepository.findById(request.academicYearId())
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found with id: " + request.academicYearId()));

        if (assignmentRepository.existsByTeacherIdAndSubjectIdAndSchoolClassIdAndAcademicYearId(
                teacher.getId(), subject.getId(), schoolClass.getId(), academicYear.getId())) {
            throw new DuplicateResourceException("Teacher is already assigned to this subject and class for the given academic year");
        }

        TeacherSubjectAssignment assignment = mapper.toEntity(teacher, subject, schoolClass, academicYear);
        TeacherSubjectAssignment saved = assignmentRepository.save(assignment);
        log.info("Assigned teacher {} to subject {} in class {}", teacher.getFullName(), subject.getName(), schoolClass.getName());

        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public void removeTeacherFromSubject(UUID assignmentId) {
        TeacherSubjectAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + assignmentId));

        assignmentRepository.delete(assignment);
        log.info("Removed teacher subject assignment with id: {}", assignmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherSubjectAssignmentResponse> getTeacherSubjects(UUID teacherId, UUID academicYearId) {
        if (!teacherRepository.existsById(teacherId)) {
            throw new ResourceNotFoundException("Teacher not found with id: " + teacherId);
        }

        if (academicYearId != null) {
            return assignmentRepository.findAllByTeacherIdAndAcademicYearId(teacherId, academicYearId).stream()
                    .map(mapper::toDto)
                    .toList();
        }
        return assignmentRepository.findAllByTeacherId(teacherId).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherSubjectAssignmentResponse> getClassSubjects(UUID classId, UUID academicYearId) {
        if (!classRepository.existsById(classId)) {
            throw new ResourceNotFoundException("Class not found with id: " + classId);
        }

        return assignmentRepository.findAllBySchoolClassIdAndAcademicYearId(classId, academicYearId).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherSubjectAssignmentResponse> getSubjectTeachers(UUID subjectId, UUID academicYearId) {
        if (!subjectRepository.existsById(subjectId)) {
            throw new ResourceNotFoundException("Subject not found with id: " + subjectId);
        }

        return assignmentRepository.findAllBySubjectIdAndAcademicYearId(subjectId, academicYearId).stream()
                .map(mapper::toDto)
                .toList();
    }
}
