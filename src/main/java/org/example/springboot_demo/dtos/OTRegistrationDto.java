package org.example.springboot_demo.dtos;

import lombok.*;
import org.example.springboot_demo.entities.EmployeeEntity;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OTRegistrationDto {
    private long otRegistrationId;
    private long employeeId;
    private String employee;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private String reason;
}
