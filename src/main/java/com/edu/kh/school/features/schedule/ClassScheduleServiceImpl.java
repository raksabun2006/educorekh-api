package com.edu.kh.school.features.schedule;

import com.edu.kh.school.exception.BusinessException;
import com.edu.kh.school.exception.InvalidStatusException;
import com.edu.kh.school.exception.ResourceNotFoundException;
import com.edu.kh.school.features.academic.AcademicYear;
import com.edu.kh.school.features.academic.AcademicYearRepository;
import com.edu.kh.school.features.schedule.dto.ClassScheduleResponse;
import com.edu.kh.school.features.schedule.dto.CreateScheduleRequest;
import com.edu.kh.school.features.schedule.dto.UpdateScheduleRequest;
import com.edu.kh.school.features.schoolclass.ClassStatus;
import com.edu.kh.school.features.schoolclass.SchoolClass;
import com.edu.kh.school.features.schoolclass.SchoolClassRepository;
import com.edu.kh.school.features.subject.Subject;
import com.edu.kh.school.features.subject.SubjectRepository;
import com.edu.kh.school.features.subject.SubjectStatus;
import com.edu.kh.school.features.teacher.Teacher;
import com.edu.kh.school.features.teacher.TeacherRepository;
import com.edu.kh.school.features.teacher.TeacherStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClassScheduleServiceImpl implements ClassScheduleService {

    private final ClassScheduleRepository scheduleRepository;
    private final SchoolClassRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final AcademicYearRepository academicYearRepository;
    private final ClassScheduleMapper mapper;

    @Override
    @Transactional
    public ClassScheduleResponse createSchedule(CreateScheduleRequest request) {
        if (!request.startTime().isBefore(request.endTime())) {
            throw new BusinessException("Schedule start time (" + request.startTime() + ") must be before end time (" + request.endTime() + ")");
        }

        SchoolClass schoolClass = classRepository.findById(request.classId())
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + request.classId()));

        if (schoolClass.getStatus() != ClassStatus.ACTIVE) {
            throw new InvalidStatusException("Cannot create schedule for inactive class: " + schoolClass.getName());
        }

        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + request.subjectId()));

        if (subject.getStatus() != SubjectStatus.ACTIVE) {
            throw new InvalidStatusException("Cannot create schedule for inactive subject: " + subject.getName());
        }

        Teacher teacher = teacherRepository.findById(request.teacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + request.teacherId()));

        if (teacher.getStatus() != TeacherStatus.ACTIVE) {
            throw new InvalidStatusException("Cannot assign inactive teacher (Status: " + teacher.getStatus() + ") to schedule");
        }

        AcademicYear academicYear = academicYearRepository.findById(request.academicYearId())
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found with id: " + request.academicYearId()));

        // Check conflicts
        if (scheduleRepository.existsTeacherScheduleConflict(
                teacher.getId(), request.dayOfWeek(), request.startTime(), request.endTime(), academicYear.getId(), null)) {
            throw new BusinessException("Teacher " + teacher.getFullName() + " has an overlapping schedule on " + request.dayOfWeek());
        }

        if (scheduleRepository.existsClassScheduleConflict(
                schoolClass.getId(), request.dayOfWeek(), request.startTime(), request.endTime(), academicYear.getId(), null)) {
            throw new BusinessException("Class " + schoolClass.getName() + " already has a schedule at this time on " + request.dayOfWeek());
        }

        if (scheduleRepository.existsRoomScheduleConflict(
                request.room().trim(), request.dayOfWeek(), request.startTime(), request.endTime(), academicYear.getId(), null)) {
            throw new BusinessException("Room " + request.room() + " is already occupied at this time on " + request.dayOfWeek());
        }

        ClassSchedule schedule = mapper.toEntity(request, schoolClass, subject, teacher, academicYear);
        ClassSchedule saved = scheduleRepository.save(schedule);
        log.info("Created class schedule with id: {}", saved.getId());

        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ClassScheduleResponse getScheduleById(UUID id) {
        ClassSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + id));
        return mapper.toDto(schedule);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassScheduleResponse> getClassSchedule(UUID classId, UUID academicYearId) {
        if (!classRepository.existsById(classId)) {
            throw new ResourceNotFoundException("Class not found with id: " + classId);
        }

        return scheduleRepository.findAllBySchoolClassIdAndAcademicYearId(classId, academicYearId).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassScheduleResponse> getTeacherSchedule(UUID teacherId, UUID academicYearId) {
        if (!teacherRepository.existsById(teacherId)) {
            throw new ResourceNotFoundException("Teacher not found with id: " + teacherId);
        }

        return scheduleRepository.findAllByTeacherIdAndAcademicYearId(teacherId, academicYearId).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ClassScheduleResponse updateSchedule(UUID id, UpdateScheduleRequest request) {
        ClassSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + id));

        if (!request.startTime().isBefore(request.endTime())) {
            throw new BusinessException("Start time must be before end time");
        }

        SchoolClass schoolClass = classRepository.findById(request.classId())
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + request.classId()));

        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + request.subjectId()));

        Teacher teacher = teacherRepository.findById(request.teacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + request.teacherId()));

        if (teacher.getStatus() != TeacherStatus.ACTIVE) {
            throw new InvalidStatusException("Teacher is not active");
        }

        AcademicYear academicYear = academicYearRepository.findById(request.academicYearId())
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found with id: " + request.academicYearId()));

        // Check conflicts excluding current schedule
        if (scheduleRepository.existsTeacherScheduleConflict(
                teacher.getId(), request.dayOfWeek(), request.startTime(), request.endTime(), academicYear.getId(), id)) {
            throw new BusinessException("Teacher " + teacher.getFullName() + " has an overlapping schedule on " + request.dayOfWeek());
        }

        if (scheduleRepository.existsClassScheduleConflict(
                schoolClass.getId(), request.dayOfWeek(), request.startTime(), request.endTime(), academicYear.getId(), id)) {
            throw new BusinessException("Class " + schoolClass.getName() + " already has a schedule at this time on " + request.dayOfWeek());
        }

        if (scheduleRepository.existsRoomScheduleConflict(
                request.room().trim(), request.dayOfWeek(), request.startTime(), request.endTime(), academicYear.getId(), id)) {
            throw new BusinessException("Room " + request.room() + " is already occupied at this time on " + request.dayOfWeek());
        }

        schedule.setSchoolClass(schoolClass);
        schedule.setSubject(subject);
        schedule.setTeacher(teacher);
        schedule.setRoom(request.room().trim());
        schedule.setDayOfWeek(request.dayOfWeek());
        schedule.setStartTime(request.startTime());
        schedule.setEndTime(request.endTime());
        schedule.setAcademicYear(academicYear);

        ClassSchedule saved = scheduleRepository.save(schedule);
        log.info("Updated class schedule with id: {}", saved.getId());

        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteSchedule(UUID id) {
        ClassSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + id));

        scheduleRepository.delete(schedule);
        log.info("Deleted class schedule with id: {}", id);
    }
}
