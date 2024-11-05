package org.example.springboot_demo.models;

import lombok.*;

import java.time.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceModel {
    private LocalDate date = LocalDate.now();
    private LocalTime checkIn;
    private String checkInStatus;
    private LocalTime checkOut;
    private String checkOutStatus;
    private String notes;
    private long employeeId;
    private boolean isPaidLeave;
    private List<LocalTime> checkInTimes;
    private boolean isHalfDay;
    private boolean isOvertime;
}

