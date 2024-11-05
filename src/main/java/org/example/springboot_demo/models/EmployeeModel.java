package org.example.springboot_demo.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeModel {
    private long employeeId;
    private String name;
    private int unusedPaidLeaves = 0;
}
