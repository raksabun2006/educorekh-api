package com.edu.kh.school.features.student;

import com.edu.kh.school.exception.DuplicateResourceException;
import com.edu.kh.school.features.auth.Sex;
import com.edu.kh.school.features.auth.UserRepository;
import com.edu.kh.school.features.schoolclass.SchoolClass;
import com.edu.kh.school.features.schoolclass.SchoolClassRepository;
import com.edu.kh.school.features.student.dto.CreateStudentRequest;
import com.edu.kh.school.features.student.dto.StudentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private SchoolClassRepository classRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private StudentMapper mapper = new StudentMapper();

    @InjectMocks
    private StudentServiceImpl studentService;

    private CreateStudentRequest request;

    @BeforeEach
    void setUp() {
        request = CreateStudentRequest.builder()
                .studentCode("STU-2026-0001")
                .fullName("Sok San")
                .email("sok.san@school.edu.kh")
                .phone("012345678")
                .dateOfBirth(LocalDate.of(2008, 5, 15))
                .sex(Sex.MALE)
                .address("Phnom Penh")
                .admissionDate(LocalDate.now())
                .build();
    }

    @Test
    void testCreateStudentSuccess() {
        when(studentRepository.existsByStudentCode("STU-2026-0001")).thenReturn(false);
        when(studentRepository.existsByEmail("sok.san@school.edu.kh")).thenReturn(false);

        Student student = mapper.toEntity(request, null);
        student.setId(UUID.randomUUID());
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        StudentResponse response = studentService.createStudent(request);

        assertNotNull(response);
        assertEquals("STU-2026-0001", response.studentCode());
        assertEquals("Sok San", response.fullName());
        assertEquals(StudentStatus.ACTIVE, response.status());
    }

    @Test
    void testCreateStudentDuplicateCode() {
        when(studentRepository.existsByStudentCode("STU-2026-0001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> studentService.createStudent(request));
    }
}
