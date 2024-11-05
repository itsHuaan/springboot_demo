package org.example.springboot_demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
    @ElementCollection
    private List<LocalTime> checkInTimes = new ArrayList<>();
    private String checkInStatus;
    private LocalTime checkOut;
    private String checkOutStatus;
    private String notes;
    private boolean isPaidLeave;
    private boolean isHalfDay;
    private boolean isOvertime;
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeEntity employee;
}

