package org.example.springboot_demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_employee")
public class EmployeeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long employeeId;

    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @Email(message = "Email is not valid", regexp = "^[\\\\w!#$%&amp;'*+/=?`{|}~^-]+(?:\\\\.[\\\\w!#$%&amp;'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\\\.)+[a-zA-Z]{2,6}$")
    @NotEmpty(message = "Email cannot be empty")
    private String email;

    private int unusedPaidLeaves;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<AttendanceEntity> attendanceList;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OTRegistrationEntity> otRegistrationList;
}
