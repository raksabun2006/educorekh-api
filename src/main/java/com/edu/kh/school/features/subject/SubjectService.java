package com.edu.kh.school.features.subject;

import com.edu.kh.school.features.subject.dto.CreateSubjectRequest;
import com.edu.kh.school.features.subject.dto.SubjectResponse;
import com.edu.kh.school.features.subject.dto.UpdateSubjectRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SubjectService {

    SubjectResponse createSubject(CreateSubjectRequest request);

    SubjectResponse getSubjectById(UUID id);

    SubjectResponse getSubjectByCode(String code);

    Page<SubjectResponse> getAllSubjects(Pageable pageable);

    Page<SubjectResponse> searchSubjects(String keyword, SubjectStatus status, Pageable pageable);

    SubjectResponse updateSubject(UUID id, UpdateSubjectRequest request);

    SubjectResponse changeSubjectStatus(UUID id, SubjectStatus status);

    void deleteSubject(UUID id);
}
