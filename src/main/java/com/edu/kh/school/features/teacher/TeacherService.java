package com.edu.kh.school.features.teacher;

import com.edu.kh.school.features.teacher.dto.CreateTeacherRequest;
import com.edu.kh.school.features.teacher.dto.TeacherResponse;
import com.edu.kh.school.features.teacher.dto.TeacherSummaryResponse;
import com.edu.kh.school.features.teacher.dto.UpdateTeacherRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TeacherService {

    TeacherResponse createTeacher(CreateTeacherRequest request);

    TeacherResponse getTeacherById(UUID id);

    TeacherResponse getTeacherByCode(String code);

    Page<TeacherResponse> getAllTeachers(Pageable pageable);

    Page<TeacherResponse> searchTeachers(String keyword, TeacherStatus status, Pageable pageable);

    TeacherResponse updateTeacher(UUID id, UpdateTeacherRequest request);

    TeacherResponse changeTeacherStatus(UUID id, TeacherStatus status);

    void deleteTeacher(UUID id);
}
