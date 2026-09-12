package com.edu.kh.school.features.admission;

import com.edu.kh.school.features.academic.AcademicYear;
import com.edu.kh.school.features.auth.Sex;
import com.edu.kh.school.features.student.Student;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "admission_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdmissionApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String applicationNumber;

    @Column(nullable = false, length = 155)
    private String applicantFullName;

    @Column(nullable = false, length = 150)
    private String applicantEmail;

    @Column(length = 25)
    private String applicantPhone;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Sex sex;

    @Column(length = 255)
    private String address;

    @Column(length = 150)
    private String previousSchool;

    @Column(nullable = false, length = 20)
    private String applyingForGrade;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AdmissionStatus status = AdmissionStatus.PENDING;

    @Column(length = 500)
    private String reviewNotes;

    @Column(length = 100)
    private String reviewedBy;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_student_id")
    private Student createdStudent;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = AdmissionStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
