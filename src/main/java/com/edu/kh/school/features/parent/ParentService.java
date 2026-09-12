package com.edu.kh.school.features.parent;

import com.edu.kh.school.features.parent.dto.CreateParentRequest;
import com.edu.kh.school.features.parent.dto.ParentResponse;
import com.edu.kh.school.features.parent.dto.UpdateParentRequest;
import com.edu.kh.school.features.student.dto.StudentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ParentService {

    ParentResponse createParent(CreateParentRequest request);

    ParentResponse getParentById(UUID id);

    Page<ParentResponse> getAllParents(Pageable pageable);

    Page<ParentResponse> searchParents(String keyword, ParentStatus status, Pageable pageable);

    ParentResponse updateParent(UUID id, UpdateParentRequest request);

    ParentResponse linkStudentToParent(UUID parentId, UUID studentId);

    ParentResponse unlinkStudentFromParent(UUID parentId, UUID studentId);

    List<StudentResponse> getChildrenByParent(UUID parentId);
}
