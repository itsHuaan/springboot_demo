package org.example.springboot_demo.models;

import jakarta.persistence.*;
import lombok.*;
import org.example.springboot_demo.entities.EmployeeEntity;
import org.example.springboot_demo.services.impl.OTRegistrationService;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OTRegistrationModel {
    private Long employeeId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status = IOTRegistrationStatus.pending.toString();
    private String reason;
}
