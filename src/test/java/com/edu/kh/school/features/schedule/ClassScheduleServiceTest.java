package com.edu.kh.school.features.schedule;

import com.edu.kh.school.exception.BusinessException;
import com.edu.kh.school.features.academic.AcademicYear;
import com.edu.kh.school.features.academic.AcademicYearRepository;
import com.edu.kh.school.features.academic.AcademicYearStatus;
import com.edu.kh.school.features.schedule.dto.ClassScheduleResponse;
import com.edu.kh.school.features.schedule.dto.CreateScheduleRequest;
import com.edu.kh.school.features.schoolclass.ClassStatus;
import com.edu.kh.school.features.schoolclass.SchoolClass;
import com.edu.kh.school.features.schoolclass.SchoolClassRepository;
import com.edu.kh.school.features.subject.Subject;
import com.edu.kh.school.features.subject.SubjectRepository;
import com.edu.kh.school.features.subject.SubjectStatus;
import com.edu.kh.school.features.teacher.Teacher;
import com.edu.kh.school.features.teacher.TeacherRepository;
import com.edu.kh.school.features.teacher.TeacherStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassScheduleServiceTest {

    @Mock
    private ClassScheduleRepository scheduleRepository;

    @Mock
    private SchoolClassRepository classRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private AcademicYearRepository academicYearRepository;

    @Spy
    private ClassScheduleMapper mapper = new ClassScheduleMapper();

    @InjectMocks
    private ClassScheduleServiceImpl scheduleService;

    private UUID classId;
    private UUID subjectId;
    private UUID teacherId;
    private UUID academicYearId;
    private SchoolClass schoolClass;
    private Subject subject;
    private Teacher teacher;
    private AcademicYear academicYear;
    private CreateScheduleRequest request;

    @BeforeEach
    void setUp() {
        classId = UUID.randomUUID();
        subjectId = UUID.randomUUID();
        teacherId = UUID.randomUUID();
        academicYearId = UUID.randomUUID();

        academicYear = AcademicYear.builder().id(academicYearId).name("2026-2027").status(AcademicYearStatus.ACTIVE).build();
        schoolClass = SchoolClass.builder().id(classId).name("Grade 10A").status(ClassStatus.ACTIVE).build();
        subject = Subject.builder().id(subjectId).code("MATH101").name("Mathematics").credit(2).status(SubjectStatus.ACTIVE).build();
        teacher = Teacher.builder().id(teacherId).teacherCode("TCH001").fullName("Teacher John").status(TeacherStatus.ACTIVE).build();

        request = CreateScheduleRequest.builder()
                .classId(classId)
                .subjectId(subjectId)
                .teacherId(teacherId)
                .room("Room 101")
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(9, 30))
                .academicYearId(academicYearId)
                .build();
    }

    @Test
    void testCreateScheduleSuccess() {
        when(classRepository.findById(classId)).thenReturn(Optional.of(schoolClass));
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(academicYearRepository.findById(academicYearId)).thenReturn(Optional.of(academicYear));

        when(scheduleRepository.existsTeacherScheduleConflict(any(), any(), any(), any(), any())).thenReturn(false);
        when(scheduleRepository.existsClassScheduleConflict(any(), any(), any(), any(), any())).thenReturn(false);
        when(scheduleRepository.existsRoomScheduleConflict(any(), any(), any(), any(), any())).thenReturn(false);

        ClassSchedule schedule = mapper.toEntity(request, schoolClass, subject, teacher, academicYear);
        schedule.setId(UUID.randomUUID());
        when(scheduleRepository.save(any(ClassSchedule.class))).thenReturn(schedule);

        ClassScheduleResponse response = scheduleService.createSchedule(request);

        assertNotNull(response);
        assertEquals("Grade 10A", response.className());
        assertEquals("Mathematics", response.subjectName());
        assertEquals("Teacher John", response.teacherName());
        assertEquals(DayOfWeek.MONDAY, response.dayOfWeek());
    }

    @Test
    void testCreateScheduleTeacherConflict() {
        when(classRepository.findById(classId)).thenReturn(Optional.of(schoolClass));
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(academicYearRepository.findById(academicYearId)).thenReturn(Optional.of(academicYear));

        when(scheduleRepository.existsTeacherScheduleConflict(any(), any(), any(), any(), any())).thenReturn(true);

        assertThrows(BusinessException.class, () -> scheduleService.createSchedule(request));
    }
}
