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
@Table(name = "tbl_ot_registration")
public class OTRegistrationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long otRegistrationId;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeEntity employee;

    @NotEmpty(message = "Date cannot be empty")
    private LocalDate date;

    @NotEmpty(message = "Start time cannot be empty")
    private LocalTime startTime;

    @NotEmpty(message = "End time cannot be empty")
    private LocalTime endTime;

    private String status;

    private String reason;
}
