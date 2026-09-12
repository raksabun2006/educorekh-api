package com.edu.kh.school.features.schedule;

import com.edu.kh.school.features.academic.AcademicYear;
import com.edu.kh.school.features.schedule.dto.ClassScheduleResponse;
import com.edu.kh.school.features.schedule.dto.CreateScheduleRequest;
import com.edu.kh.school.features.schoolclass.SchoolClass;
import com.edu.kh.school.features.subject.Subject;
import com.edu.kh.school.features.teacher.Teacher;
import org.springframework.stereotype.Component;

@Component
public class ClassScheduleMapper {

    public ClassSchedule toEntity(CreateScheduleRequest request, SchoolClass schoolClass, Subject subject, Teacher teacher, AcademicYear academicYear) {
        return ClassSchedule.builder()
                .schoolClass(schoolClass)
                .subject(subject)
                .teacher(teacher)
                .room(request.room().trim())
                .dayOfWeek(request.dayOfWeek())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .academicYear(academicYear)
                .build();
    }

    public ClassScheduleResponse toDto(ClassSchedule schedule) {
        return ClassScheduleResponse.builder()
                .id(schedule.getId())
                .classId(schedule.getSchoolClass().getId())
                .className(schedule.getSchoolClass().getName())
                .subjectId(schedule.getSubject().getId())
                .subjectCode(schedule.getSubject().getCode())
                .subjectName(schedule.getSubject().getName())
                .teacherId(schedule.getTeacher().getId())
                .teacherCode(schedule.getTeacher().getTeacherCode())
                .teacherName(schedule.getTeacher().getFullName())
                .room(schedule.getRoom())
                .dayOfWeek(schedule.getDayOfWeek())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .academicYearId(schedule.getAcademicYear().getId())
                .academicYearName(schedule.getAcademicYear().getName())
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt())
                .build();
    }
}
