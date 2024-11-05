package org.example.springboot_demo.dtos;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceByDate {
    private LocalDate date;
    private List<AttendanceDto> attendances;
}
