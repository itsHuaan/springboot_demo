package org.example.springboot_demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_attendance")
public class AttendanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long attendanceId;
    private LocalDate date;
    private LocalTime checkIn;
    private String checkInStatus;
    private LocalTime checkOut;
    private String checkOutStatus;
    private String notes;
    private boolean isPaidLeave;
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private StudentEntity student;
}
