package com.edu.kh.school.features.schedule;

import com.edu.kh.school.features.schedule.dto.ClassScheduleResponse;
import com.edu.kh.school.features.schedule.dto.CreateScheduleRequest;
import com.edu.kh.school.features.schedule.dto.UpdateScheduleRequest;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

public interface ClassScheduleService {

    ClassScheduleResponse createSchedule(CreateScheduleRequest request);

    ClassScheduleResponse getScheduleById(UUID id);

    List<ClassScheduleResponse> getAllSchedules(UUID academicYearId, UUID classId, UUID teacherId, DayOfWeek dayOfWeek);

    List<ClassScheduleResponse> getClassSchedule(UUID classId, UUID academicYearId);

    List<ClassScheduleResponse> getTeacherSchedule(UUID teacherId, UUID academicYearId);

    ClassScheduleResponse updateSchedule(UUID id, UpdateScheduleRequest request);

    void deleteSchedule(UUID id);
}
