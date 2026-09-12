package com.edu.kh.school.features.parent;

import com.edu.kh.school.features.parent.dto.CreateParentRequest;
import com.edu.kh.school.features.parent.dto.ParentResponse;
import com.edu.kh.school.features.parent.dto.UpdateParentRequest;
import com.edu.kh.school.features.student.dto.StudentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/parents")
@RequiredArgsConstructor
public class ParentController {

    private final ParentService parentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'STAFF')")
    public ParentResponse createParent(@Valid @RequestBody CreateParentRequest request) {
        return parentService.createParent(request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ParentResponse getParentById(@PathVariable UUID id) {
        return parentService.getParentById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<ParentResponse> getAllParents(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ParentStatus status,
            Pageable pageable
    ) {
        if (keyword != null || status != null) {
            return parentService.searchParents(keyword, status, pageable);
        }
        return parentService.getAllParents(pageable);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'STAFF')")
    public ParentResponse updateParent(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateParentRequest request
    ) {
        return parentService.updateParent(id, request);
    }

    @PostMapping("/{id}/students/{studentId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'STAFF')")
    public ParentResponse linkStudent(
            @PathVariable UUID id,
            @PathVariable UUID studentId
    ) {
        return parentService.linkStudentToParent(id, studentId);
    }

    @DeleteMapping("/{id}/students/{studentId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'STAFF')")
    public ParentResponse unlinkStudent(
            @PathVariable UUID id,
            @PathVariable UUID studentId
    ) {
        return parentService.unlinkStudentFromParent(id, studentId);
    }

    @GetMapping("/{id}/children")
    @ResponseStatus(HttpStatus.OK)
    public List<StudentResponse> getChildren(@PathVariable UUID id) {
        return parentService.getChildrenByParent(id);
    }
}
