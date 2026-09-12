package com.edu.kh.school.features.schoolclass;

import com.edu.kh.school.exception.BusinessException;
import com.edu.kh.school.exception.DuplicateResourceException;
import com.edu.kh.school.exception.InvalidStatusException;
import com.edu.kh.school.exception.ResourceNotFoundException;
import com.edu.kh.school.features.academic.AcademicYear;
import com.edu.kh.school.features.academic.AcademicYearRepository;
import com.edu.kh.school.features.schoolclass.dto.ClassResponse;
import com.edu.kh.school.features.schoolclass.dto.CreateClassRequest;
import com.edu.kh.school.features.schoolclass.dto.UpdateClassRequest;
import com.edu.kh.school.features.teacher.Teacher;
import com.edu.kh.school.features.teacher.TeacherRepository;
import com.edu.kh.school.features.teacher.TeacherStatus;
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
public class SchoolClassServiceImpl implements SchoolClassService {

    private final SchoolClassRepository classRepository;
    private final AcademicYearRepository academicYearRepository;
    private final TeacherRepository teacherRepository;
    private final SchoolClassMapper mapper;

    @Override
    @Transactional
    public ClassResponse createClass(CreateClassRequest request) {
        String name = request.name().trim();

        if (classRepository.existsByNameAndAcademicYearId(name, request.academicYearId())) {
            throw new DuplicateResourceException("Class '" + name + "' already exists in this academic year");
        }

        AcademicYear academicYear = academicYearRepository.findById(request.academicYearId())
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found with id: " + request.academicYearId()));

        Teacher classTeacher = null;
        if (request.classTeacherId() != null) {
            classTeacher = teacherRepository.findById(request.classTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + request.classTeacherId()));
            if (classTeacher.getStatus() != TeacherStatus.ACTIVE) {
                throw new InvalidStatusException("Cannot assign inactive teacher (Status: " + classTeacher.getStatus() + ") as class teacher");
            }
        }

        SchoolClass schoolClass = mapper.toEntity(request, academicYear, classTeacher);
        SchoolClass saved = classRepository.save(schoolClass);
        log.info("Created class with id: {} and name: {}", saved.getId(), saved.getName());

        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ClassResponse getClassById(UUID id) {
        SchoolClass schoolClass = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + id));
        return mapper.toDto(schoolClass);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClassResponse> getAllClasses(Pageable pageable) {
        return classRepository.findAll(pageable)
                .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClassResponse> searchClasses(String keyword, UUID academicYearId, ClassStatus status, Pageable pageable) {
        return classRepository.searchClasses(
                keyword != null && !keyword.isBlank() ? keyword.trim() : null,
                academicYearId,
                status,
                pageable
        ).map(mapper::toDto);
    }

    @Override
    @Transactional
    public ClassResponse updateClass(UUID id, UpdateClassRequest request) {
        SchoolClass schoolClass = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + id));

        String name = request.name().trim();
        if (!schoolClass.getName().equalsIgnoreCase(name) &&
                classRepository.existsByNameAndAcademicYearId(name, request.academicYearId())) {
            throw new DuplicateResourceException("Class '" + name + "' already exists in this academic year");
        }

        AcademicYear academicYear = academicYearRepository.findById(request.academicYearId())
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found with id: " + request.academicYearId()));

        Teacher classTeacher = null;
        if (request.classTeacherId() != null) {
            classTeacher = teacherRepository.findById(request.classTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + request.classTeacherId()));
            if (classTeacher.getStatus() != TeacherStatus.ACTIVE) {
                throw new InvalidStatusException("Cannot assign inactive teacher as class teacher");
            }
        }

        schoolClass.setName(name);
        schoolClass.setGradeLevel(request.gradeLevel().trim());
        schoolClass.setAcademicYear(academicYear);
        schoolClass.setRoom(request.room() != null ? request.room().trim() : null);
        schoolClass.setCapacity(request.capacity());
        schoolClass.setClassTeacher(classTeacher);
        schoolClass.setStatus(request.status());

        SchoolClass saved = classRepository.save(schoolClass);
        log.info("Updated class with id: {}", saved.getId());

        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public ClassResponse assignClassTeacher(UUID classId, UUID teacherId) {
        SchoolClass schoolClass = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + classId));

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));

        if (teacher.getStatus() != TeacherStatus.ACTIVE) {
            throw new InvalidStatusException("Cannot assign teacher with status " + teacher.getStatus() + " as class teacher");
        }

        schoolClass.setClassTeacher(teacher);
        SchoolClass saved = classRepository.save(schoolClass);
        log.info("Assigned teacher {} to class {}", teacherId, classId);

        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public ClassResponse removeClassTeacher(UUID classId) {
        SchoolClass schoolClass = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + classId));

        schoolClass.setClassTeacher(null);
        SchoolClass saved = classRepository.save(schoolClass);
        log.info("Removed class teacher from class {}", classId);

        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public ClassResponse changeClassStatus(UUID classId, ClassStatus status) {
        SchoolClass schoolClass = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + classId));

        schoolClass.setStatus(status);
        SchoolClass saved = classRepository.save(schoolClass);
        log.info("Changed class {} status to: {}", classId, status);

        return mapper.toDto(saved);
    }
}
