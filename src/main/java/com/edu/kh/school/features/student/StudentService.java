package com.edu.kh.school.features.student;

import com.edu.kh.school.features.student.dto.CreateStudentRequest;
import com.edu.kh.school.features.student.dto.StudentResponse;
import com.edu.kh.school.features.student.dto.UpdateStudentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface StudentService {

    StudentResponse createStudent(CreateStudentRequest request);

    StudentResponse getStudentById(UUID id);

    StudentResponse getStudentByCode(String code);

    Page<StudentResponse> getAllStudents(Pageable pageable);

    Page<StudentResponse> searchStudents(String keyword, UUID classId, StudentStatus status, Pageable pageable);

    StudentResponse updateStudent(UUID id, UpdateStudentRequest request);

    StudentResponse changeStudentStatus(UUID id, StudentStatus status);

    void deleteStudent(UUID id);
}
