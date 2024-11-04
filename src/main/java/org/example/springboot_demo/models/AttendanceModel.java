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
    private LocalTime checkOut;
    private String notes;
    private long studentId;
}
