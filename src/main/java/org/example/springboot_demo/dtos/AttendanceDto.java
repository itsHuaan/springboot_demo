package org.example.springboot_demo.dtos;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceDto {
    private String student;
    private LocalDate date;
    private LocalTime checkIn;
    private String checkInStatus;
    private LocalTime checkOut;
    private String checkOutStatus;
    private String notes;
    private boolean isPaidLeave;
}
