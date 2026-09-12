package com.edu.kh.school.features.attendance;

import com.edu.kh.school.features.attendance.dto.AttendanceResponse;
import com.edu.kh.school.features.attendance.dto.RecordAttendanceRequest;
import com.edu.kh.school.features.schoolclass.SchoolClass;
import com.edu.kh.school.features.student.Student;
import org.springframework.stereotype.Component;

@Component
public class AttendanceMapper {

    public Attendance toEntity(RecordAttendanceRequest request, Student student, SchoolClass schoolClass, String recordedBy) {
        return Attendance.builder()
                .student(student)
                .schoolClass(schoolClass)
                .date(request.date())
                .status(request.status())
                .remarks(request.remarks())
                .recordedBy(recordedBy)
                .build();
    }

    public AttendanceResponse toDto(Attendance attendance) {
        return AttendanceResponse.builder()
                .id(attendance.getId())
                .studentId(attendance.getStudent().getId())
                .studentCode(attendance.getStudent().getStudentCode())
                .studentName(attendance.getStudent().getFullName())
                .classId(attendance.getSchoolClass().getId())
                .className(attendance.getSchoolClass().getName())
                .date(attendance.getDate())
                .status(attendance.getStatus())
                .remarks(attendance.getRemarks())
                .recordedBy(attendance.getRecordedBy())
                .createdAt(attendance.getCreatedAt())
                .updatedAt(attendance.getUpdatedAt())
                .build();
    }
}
