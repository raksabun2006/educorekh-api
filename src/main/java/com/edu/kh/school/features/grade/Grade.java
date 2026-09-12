package com.edu.kh.school.features.grade;

import com.edu.kh.school.features.academic.AcademicYear;
import com.edu.kh.school.features.subject.Subject;
import com.edu.kh.school.features.student.Student;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "grades",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"student_id", "subject_id", "academic_year_id", "semester"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Semester semester;

    @Column(nullable = false)
    private Double score;

    @Column(nullable = false, length = 5)
    private String gradeLetter;

    @Column(length = 255)
    private String remarks;

    @Column(nullable = false, length = 100)
    private String recordedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        calculateGradeLetter();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        calculateGradeLetter();
    }

    public void calculateGradeLetter() {
        if (score == null) {
            this.gradeLetter = "F";
            return;
        }
        if (score >= 90.0) {
            this.gradeLetter = "A";
        } else if (score >= 80.0) {
            this.gradeLetter = "B";
        } else if (score >= 70.0) {
            this.gradeLetter = "C";
        } else if (score >= 60.0) {
            this.gradeLetter = "D";
        } else if (score >= 50.0) {
            this.gradeLetter = "E";
        } else {
            this.gradeLetter = "F";
        }
    }
}
