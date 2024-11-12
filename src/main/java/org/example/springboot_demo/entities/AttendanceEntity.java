package org.example.springboot_demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

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

    @NotEmpty(message = "Date cannot be empty")
    private LocalDate date;

    @NotEmpty(message = "Check-In cannot be empty")
    private LocalTime checkIn;

    private String checkInStatus;

    @NotEmpty(message = "Check-Out cannot be empty")
    private LocalTime checkOut;

    private String checkOutStatus;

    private String notes;

    private boolean isPaidLeave;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeEntity employee;
}
