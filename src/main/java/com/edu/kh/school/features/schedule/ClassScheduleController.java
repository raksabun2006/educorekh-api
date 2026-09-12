package com.edu.kh.school.features.schedule;

import com.edu.kh.school.features.schedule.dto.ClassScheduleResponse;
import com.edu.kh.school.features.schedule.dto.CreateScheduleRequest;
import com.edu.kh.school.features.schedule.dto.UpdateScheduleRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ClassScheduleController {

    private final ClassScheduleService scheduleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ClassScheduleResponse createSchedule(@Valid @RequestBody CreateScheduleRequest request) {
        return scheduleService.createSchedule(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ClassScheduleResponse> getAllSchedules(
            @RequestParam(required = false) UUID academicYearId,
            @RequestParam(required = false) UUID classId,
            @RequestParam(required = false) UUID teacherId,
            @RequestParam(required = false) DayOfWeek dayOfWeek
    ) {
        return scheduleService.getAllSchedules(academicYearId, classId, teacherId, dayOfWeek);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ClassScheduleResponse getScheduleById(@PathVariable UUID id) {
        return scheduleService.getScheduleById(id);
    }

    @GetMapping("/class/{classId}")
    @ResponseStatus(HttpStatus.OK)
    public List<ClassScheduleResponse> getClassSchedule(
            @PathVariable UUID classId,
            @RequestParam(required = false) UUID academicYearId
    ) {
        return scheduleService.getClassSchedule(classId, academicYearId);
    }

    @GetMapping("/teacher/{teacherId}")
    @ResponseStatus(HttpStatus.OK)
    public List<ClassScheduleResponse> getTeacherSchedule(
            @PathVariable UUID teacherId,
            @RequestParam(required = false) UUID academicYearId
    ) {
        return scheduleService.getTeacherSchedule(teacherId, academicYearId);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ClassScheduleResponse updateSchedule(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateScheduleRequest request
    ) {
        return scheduleService.updateSchedule(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public void deleteSchedule(@PathVariable UUID id) {
        scheduleService.deleteSchedule(id);
    }
}
