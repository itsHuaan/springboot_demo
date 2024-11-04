package org.example.springboot_demo.models;

import lombok.*;

import java.time.*;

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
    private long studentId;
    private boolean isPaidLeave;
}
