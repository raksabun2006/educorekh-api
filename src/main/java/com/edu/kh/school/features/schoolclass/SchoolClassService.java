package com.edu.kh.school.features.schoolclass;

import com.edu.kh.school.features.schoolclass.dto.ClassResponse;
import com.edu.kh.school.features.schoolclass.dto.CreateClassRequest;
import com.edu.kh.school.features.schoolclass.dto.UpdateClassRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SchoolClassService {

    ClassResponse createClass(CreateClassRequest request);

    ClassResponse getClassById(UUID id);

    Page<ClassResponse> getAllClasses(Pageable pageable);

    Page<ClassResponse> searchClasses(String keyword, UUID academicYearId, ClassStatus status, Pageable pageable);

    ClassResponse updateClass(UUID id, UpdateClassRequest request);

    ClassResponse assignClassTeacher(UUID classId, UUID teacherId);

    ClassResponse removeClassTeacher(UUID classId);

    ClassResponse changeClassStatus(UUID classId, ClassStatus status);
}
