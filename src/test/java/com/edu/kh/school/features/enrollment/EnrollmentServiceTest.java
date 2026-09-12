package com.edu.kh.school.features.enrollment;

import com.edu.kh.school.exception.CapacityExceededException;
import com.edu.kh.school.exception.DuplicateResourceException;
import com.edu.kh.school.features.academic.AcademicYear;
import com.edu.kh.school.features.academic.AcademicYearRepository;
import com.edu.kh.school.features.academic.AcademicYearStatus;
import com.edu.kh.school.features.enrollment.dto.EnrollStudentRequest;
import com.edu.kh.school.features.enrollment.dto.EnrollmentResponse;
import com.edu.kh.school.features.schoolclass.ClassStatus;
import com.edu.kh.school.features.schoolclass.SchoolClass;
import com.edu.kh.school.features.schoolclass.SchoolClassRepository;
import com.edu.kh.school.features.student.Student;
import com.edu.kh.school.features.student.StudentRepository;
import com.edu.kh.school.features.student.StudentStatus;
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
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private SchoolClassRepository classRepository;

    @Mock
    private AcademicYearRepository academicYearRepository;

    @Spy
    private EnrollmentMapper mapper = new EnrollmentMapper();

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    private UUID studentId;
    private UUID classId;
    private UUID academicYearId;
    private Student student;
    private SchoolClass schoolClass;
    private AcademicYear academicYear;
    private EnrollStudentRequest request;

    @BeforeEach
    void setUp() {
        studentId = UUID.randomUUID();
        classId = UUID.randomUUID();
        academicYearId = UUID.randomUUID();

        academicYear = AcademicYear.builder()
                .id(academicYearId)
                .name("2026-2027")
                .status(AcademicYearStatus.ACTIVE)
                .build();

        schoolClass = SchoolClass.builder()
                .id(classId)
                .name("Grade 10A")
                .gradeLevel("10")
                .academicYear(academicYear)
                .capacity(30)
                .status(ClassStatus.ACTIVE)
                .build();

        student = Student.builder()
                .id(studentId)
                .studentCode("STU-2026-0001")
                .fullName("Sok San")
                .status(StudentStatus.ACTIVE)
                .build();

        request = EnrollStudentRequest.builder()
                .studentId(studentId)
                .classId(classId)
                .academicYearId(academicYearId)
                .enrollmentDate(LocalDate.now())
                .build();
    }

    @Test
    void testEnrollStudentSuccess() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(classRepository.findById(classId)).thenReturn(Optional.of(schoolClass));
        when(academicYearRepository.findById(academicYearId)).thenReturn(Optional.of(academicYear));
        when(enrollmentRepository.existsByStudentIdAndAcademicYearIdAndStatus(studentId, academicYearId, EnrollmentStatus.ACTIVE)).thenReturn(false);
        when(enrollmentRepository.countBySchoolClassIdAndStatus(classId, EnrollmentStatus.ACTIVE)).thenReturn(10L);

        Enrollment enrollment = mapper.toEntity(request, student, schoolClass, academicYear);
        enrollment.setId(UUID.randomUUID());
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);

        EnrollmentResponse response = enrollmentService.enrollStudent(request);

        assertNotNull(response);
        assertEquals("Grade 10A", response.className());
        assertEquals("STU-2026-0001", response.studentCode());
        assertEquals(EnrollmentStatus.ACTIVE, response.status());
    }

    @Test
    void testEnrollStudentDuplicateActiveEnrollment() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(classRepository.findById(classId)).thenReturn(Optional.of(schoolClass));
        when(academicYearRepository.findById(academicYearId)).thenReturn(Optional.of(academicYear));
        when(enrollmentRepository.existsByStudentIdAndAcademicYearIdAndStatus(studentId, academicYearId, EnrollmentStatus.ACTIVE)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> enrollmentService.enrollStudent(request));
    }

    @Test
    void testEnrollStudentCapacityExceeded() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(classRepository.findById(classId)).thenReturn(Optional.of(schoolClass));
        when(academicYearRepository.findById(academicYearId)).thenReturn(Optional.of(academicYear));
        when(enrollmentRepository.existsByStudentIdAndAcademicYearIdAndStatus(studentId, academicYearId, EnrollmentStatus.ACTIVE)).thenReturn(false);
        when(enrollmentRepository.countBySchoolClassIdAndStatus(classId, EnrollmentStatus.ACTIVE)).thenReturn(30L);

        assertThrows(CapacityExceededException.class, () -> enrollmentService.enrollStudent(request));
    }
}
